package org.pfyu.testfix.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.pfyu.testfix.data.html.TR;

public class TestFixDetailReader extends Thread {

	private CountDownLatch signal;
	private String detailURL;
	private TR tr;

	public TestFixDetailReader(CountDownLatch signal, String detailURL, TR tr) {
		this.signal = signal;
		this.detailURL = detailURL;
		this.tr = tr;
	}

	public void run() {
		System.out.println("*************Start to parse: " + detailURL);
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(detailURL).openConnection();

			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setAllowUserInteraction(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryO7FcS0lBO8MZik1A");
			con.connect();

			// Send the credential data to the server using HTTP POST
			String dataFileName = "C:\\TestFixData\\data_detail.txt";
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(new File(dataFileName)));
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				// Replace the old token with the current one
				if (line.contains("RBD-")) {
					int start = detailURL.indexOf("=");
					line = detailURL.substring(start + 1, detailURL.length());
				}
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

			// set details into the TR
			tr.setDetails(output);

			// count down by 1;
			signal.countDown();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
