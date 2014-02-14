package org.pfyu.testfix.scan.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.pfyu.testfix.data.entity.Testfix;
import org.pfyu.testfix.scan.TestFixReader;
import org.pfyu.testfix.scan.TestFixScanner;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScanJob implements Job {

	private BlockingQueue<File> zipFileQueue;
	private ExecutorService es;
	private Map<String, List<String>> createdOnReleasesCache;
	private Map<String, String> submittedByCache;
	private Map<String, String> pmrCache;
	private Map<String, String> aparCache;
	private Map<String, String> customerCache;
	private Map<String, String> descriptionCache;
	public static Map<String, Testfix> testFixMsg = new ConcurrentHashMap<String, Testfix>();

	public ScanJob() {
		zipFileQueue = new LinkedBlockingQueue<File>();
		es = Executors.newCachedThreadPool();
		createdOnReleasesCache = new ConcurrentHashMap<String, List<String>>();
		submittedByCache = new ConcurrentHashMap<>();
		pmrCache = new ConcurrentHashMap<>();
		aparCache = new ConcurrentHashMap<>();
		customerCache = new ConcurrentHashMap<>();
		descriptionCache = new ConcurrentHashMap<>();
	}

	private void readExtraInfo() {
		File extraInfoFile = new File("C:\\TestFixData\\extrainfo.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(extraInfoFile));
			while (true) {
				String line = br.readLine();
				if (line == null || line.trim().length() == 0) {
					break;
				}

				String[] array = line.split("\\^");
				String testFixName = array[0];

				// Process Created On
				String createdOnReleasesStr = array[1];
				createdOnReleasesStr = createdOnReleasesStr.replace(".", "").replace("RBD", "");

				List<String> list = new ArrayList<String>();
				if (createdOnReleasesStr.indexOf("/") != -1) {
					String[] releases = createdOnReleasesStr.split("/");
					for (String release : releases) {
						list.add(release);
					}
				} else if (createdOnReleasesStr.indexOf(",") != -1) {
					String[] releases = createdOnReleasesStr.split(",");
					for (String release : releases) {
						list.add(release);
					}
				} else if (createdOnReleasesStr.indexOf(";") != -1) {
					String[] releases = createdOnReleasesStr.split(";");
					for (String release : releases) {
						list.add(release);
					}
				} else {
					list.add(createdOnReleasesStr);
				}
				createdOnReleasesCache.put(testFixName, list);

				// Process Submitted By
				String submittedBy = array[2].trim();
				submittedByCache.put(testFixName, submittedBy);

				// Process PMR
				String pmr = array[3].trim();
				pmrCache.put(testFixName, pmr);

				// Process APAR
				String apar = array[4].trim();
				aparCache.put(testFixName, apar);

				// Process customer
				String customer = array[5].trim();
				customerCache.put(testFixName, customer);

				// Process description
				String dsc = null;
				if (array.length >= 7) {
					dsc = array[6].trim();
					descriptionCache.put(testFixName, dsc);
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void scanTestFix(String directory) throws InterruptedException {
		File root = new File(directory);
		TestFixScanner scanner = new TestFixScanner(root, zipFileQueue);
		es.submit(scanner);
	}

	public void readTestFix() {
		for (int i = 0; i < 500; i++) {
			TestFixReader reader = new TestFixReader(zipFileQueue, createdOnReleasesCache, submittedByCache, pmrCache, aparCache, customerCache, descriptionCache);
			es.submit(reader);
		}
	}

	public BlockingQueue<File> getZipFileQueue() {
		return zipFileQueue;
	}

	public void setZipFileQueue(BlockingQueue<File> zipFileQueue) {
		this.zipFileQueue = zipFileQueue;
	}

	public void await() {
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		es.shutdown();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("**********scan job is running**************");
		System.out.println("**********reset existingTestFixNamesInDB**************");
		ScanScheduler.getInstance().clear();
		ScanScheduler.getInstance().getExistingTestFixNamesInDB();

		// read extra info from temp file, and put them in cache
		System.out.println("**********1. read extra info from temp file and put into the cache***************");
		readExtraInfo();
		System.out.println("**********1. read extra info from temp file and put into the cache finished***************");

		// scan all the zip files in the disk
		System.out.println("*************2. scan all the zip files in the disk, put each File object to the blocking queue*************");
		try {
			scanTestFix("C:\\TestFixData\\testfixoutput");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// concurrent reading zip files from the blocking queue
		System.out.println("*************3. concurrent reading zip files from the blocking queue, insert the result to the database**************************");
		readTestFix();

		try {
			Thread.sleep(120000);
			StringBuilder html = new StringBuilder();
			html.append("<html>").append("\r\n");
			html.append(" <header/>").append("\r\n");
			html.append("  <body>").append("\r\n");
			if (testFixMsg.isEmpty()) {
				html.append("<b>No Scanning Result at " + new Date()).append("\r\n");
			} else {
				html.append("<b>There are totally <font color=\"green\">" + testFixMsg.size() + "</font> new Test Fixes at " + new Date()).append("\r\n");
				html.append("   <table border=\"1\">").append("\r\n");
				for (String testFixName : testFixMsg.keySet()) {
					html.append("    <tr>").append("\r\n");
					Testfix tf = testFixMsg.get(testFixName);
					html.append("     <td>").append(tf.getTestfixname()).append("</td>").append("\r\n");
					html.append("     <td>").append(tf.getAparname()).append("</td>").append("\r\n");
					html.append("     <td>").append(tf.getCreatedon()).append("</td>").append("\r\n");
					html.append("    </tr>").append("\r\n");
				}
				html.append("   </table>").append("\r\n");
			}
			html.append("  </body>").append("\r\n");
			html.append("</html>");
			sendMessage(html.toString());
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
			testFixMsg.clear();
		}

		shutdown();
		await();
	}

	private void sendMessage(String msg) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException, InterruptedException {
		URL url = new URL("http://localhost:8080/TestFixMessageSender/MessageSender");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setAllowUserInteraction(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "text/plain");
		con.connect();

		OutputStream os = con.getOutputStream();
		os.write(msg.getBytes("UTF-8"));
		os.close();

		Thread.sleep(10000);

		List<String> output = new ArrayList<String>();
		InputStream is = con.getInputStream();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(is));
		while (true) {
			String line = bufreader.readLine();
			if (line == null)
				break;
			output.add(line);
		}

		System.out.println("TestFixWeb: Message is sent out <" + output + ">");
	}

}
