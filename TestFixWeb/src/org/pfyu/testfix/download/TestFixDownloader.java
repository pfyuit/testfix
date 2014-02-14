package org.pfyu.testfix.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pfyu.testfix.data.html.TD;
import org.pfyu.testfix.data.html.TR;
import org.pfyu.testfix.download.schedule.DownloadScheduler;

public class TestFixDownloader implements IDownloader {

	private ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public List<String> downloadHTML(URL url) throws IOException, InterruptedException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setAllowUserInteraction(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------7dd35333b607a0");
		con.connect();

		// send the credential data to the server using HTTP POST
		String dataFileName = "C:\\TestFixData\\data.txt";
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new File(dataFileName)));
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			sb.append(line);
			sb.append("\r\n");
		}
		br.close();
		OutputStream os = con.getOutputStream();
		os.write(sb.toString().getBytes());
		os.close();

		Thread.sleep(10000);

		// obtain the HTML response from the server
		List<String> output = new ArrayList<String>();
		InputStream is = con.getInputStream();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(is));
		while (true) {
			String line = bufreader.readLine();
			if (line == null)
				break;
			output.add(line);
		}
		return output;
	}

	@Override
	public List<TR> parseHTML(List<String> html) {
		List<TR> trList = new ArrayList<TR>();

		for (int i = 0; i < html.size(); i++) {
			String line = html.get(i);
			if (line.equalsIgnoreCase("<td><font color=green><b>On FTP</b></font></td>")) {
				TR tr = new TR();
				for (int j = i; j <= i + 12; j++) {
					String tdLine = html.get(j).trim();
					int start = tdLine.indexOf("<td>");
					int end = tdLine.indexOf("</td>");
					if (end == -1)
						end = tdLine.length();
					String data = tdLine.substring(start + 4, end);
					if (data == null || data.trim().length() == 0)
						data = "N/A";
					TD td = new TD();
					td.setData(data);
					tr.addTD(td);
				}
				trList.add(tr);
			}
		}

		// after the basic HTML is parsed, then download extra "View Details"
		System.out.println("****Parsed basic HTML successfully, now try to parse detailed HTML for new test fix");
		Map<String, TR> newTRs = new HashMap<>();
		for (TR tr : trList) {
			List<TD> tdList = tr.getTdList();
			TD zipURL = tdList.get(10);
			if (!zipURL.getData().startsWith("<a href=")) {
				zipURL = tdList.get(11);
			}

			int start = zipURL.getData().indexOf("\'");
			int end = zipURL.getData().lastIndexOf("\'");
			String url = zipURL.getData().substring(start + 1, end);
			int endind = url.lastIndexOf("/");
			String testFixName = url.substring(endind + 1, url.length() - 4);

			// if disk already contains this testfix, skip it
			if (DownloadScheduler.getInstance().getExistingTestFixNamesInDisk().contains(testFixName)) {
				continue;
			}

			System.out.println("****A new Test fix detected");
			int startIndex = url.indexOf("RBD");
			int endIndex = url.lastIndexOf("/");
			String token = url.substring(startIndex, endIndex);
			String detailURL = "http://rcs-testfix.ratl.swg.usma.ibm.com/cgi-bin/fix_portal.pl?do_view=" + token;

			newTRs.put(detailURL, tr);
		}
		if (newTRs.size() > 0) {
			CountDownLatch threads = new CountDownLatch(newTRs.size());
			for (String detailURL : newTRs.keySet()) {
				new TestFixDetailReader(threads, detailURL, newTRs.get(detailURL)).start();
			}
			try {
				threads.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("****All the detailed HTMLs are parsed successfully: " + newTRs.size());
		} else {
			System.out.println("****There is no new Test fix");
		}

		return trList;
	}

	@Override
	public void downloadZip(List<TR> trList, String outputDic) {
		StringBuilder sb = new StringBuilder();
		List<String> testFixNames = new ArrayList<String>();
		for (TR tr : trList) {
			List<TD> tdList = tr.getTdList();
			TD zipURL = tdList.get(10);
			if (!zipURL.getData().startsWith("<a href=")) {
				zipURL = tdList.get(11);
			}
			int start = zipURL.getData().indexOf("\'");
			int end = zipURL.getData().lastIndexOf("\'");
			String url = zipURL.getData().substring(start + 1, end);

			// extract extra info and then write to a temp file
			TD createdOnReleases = tdList.get(2);
			String createdOnReleasesStr = createdOnReleases.getData();
			int endind = url.lastIndexOf("/");
			String testFixName = url.substring(endind + 1, url.length() - 4);

			// If disk already contains this testfix, skip it
			if (DownloadScheduler.getInstance().getExistingTestFixNamesInDisk().contains(testFixName)) {
				System.out.println("disk already contains this testfix, skip");
				continue;
			}

			String submittedBy = null, company = null, pmr = null, apar = null, description = null;
			for (int i = 0; i < tr.getDetails().size(); i++) {
				String detail = tr.getDetails().get(i);

				// Extract Submitted By information
				if (detail.indexOf("<td><b>Submitted By</td>") > -1) {
					String next = tr.getDetails().get(i + 1);
					int a = next.indexOf("<td>");
					submittedBy = next.substring(a + 4);
				}

				// Extract Test Fix Description
				if (detail.indexOf("<td valign=top><b>Fix Description (APARs/RATLC Defects)</td>") > -1) {
					String next = tr.getDetails().get(i + 1);
					int a = next.indexOf("<td>");
					int b = next.indexOf("<br>");
					if (b == -1) {
						description = next.substring(a + 4);
					} else {
						description = next.substring(a + 4, b);
					}

					if (description.contains("<a href=")) {
						int s = description.indexOf("<a href=");
						int e = description.indexOf("\">");
						String subString = description.substring(s, e + 2);
						description = description.replace(subString, "");
						description = description.replace("</a>", "");
					}
				}

				// Extract Company in the Customer Notified area if exist.
				if (detail.indexOf("<b>Company:</b>") > -1) {
					int a = detail.indexOf("</b>");
					int b = detail.indexOf("<br>");
					company = detail.substring(a + 4, b);
				}

				// Extract PMR number in the Customer Notified area if exist.
				if (detail.indexOf("<b>PMRs:</b>") > -1) {
					int a = detail.indexOf("</b>");
					int b = detail.indexOf("&nbsp;");
					pmr = detail.substring(a + 4, b);
				}

				// Extract APAR number, use the "APAR List" first, if it not exist, then go the the "APARs" in the Customer Notified area.
				if (detail.indexOf("<td valign=top><b>APAR List</td>") > -1) {
					String next = tr.getDetails().get(i + 1);
					int a = next.indexOf("<td>");
					int b = next.indexOf("</td>");
					apar = next.substring(a + 4, b);
				} else if (detail.indexOf("<b>APARs:</b>") > -1) {
					int a = detail.indexOf("<b>APARs:</b>");
					int b = detail.indexOf("<br>");
					if (b == -1) {
						apar = detail.substring(a + 13);
					} else {
						apar = detail.substring(a + 13, b);
					}
				}
			}

			sb.append(testFixName).append("^").append(createdOnReleasesStr).append("^").append(submittedBy).append("^").append(pmr).append("^").append(apar).append("^")
					.append(company).append("^").append(description).append("\r\n");

			testFixNames.add(testFixName);

			// For each testfix, assign to a thread to download it
			ZipDownloader zd = new ZipDownloader(url, outputDic);
			executor.submit(zd);
		}

		// Append to a temp file
		File extraInfoFile = new File("C:\\TestFixData\\extrainfo.txt");
		StringBuilder old = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(extraInfoFile));
			while (true) {
				String line = br.readLine();

				// To the end of file
				if (line == null || line.trim().length() == 0) {
					break;
				}

				// Replace the old test fix entry with new one
				boolean needReplace = false;
				for (String fix : testFixNames) {
					if (line.startsWith(fix.trim())) {
						needReplace = true;
						break;
					}
				}
				if (needReplace) {
					continue;
				}

				old.append(line).append("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(extraInfoFile));
			pw.print(old.append(sb).toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}

		DownloadScheduler.getInstance().clear();
	}

}
