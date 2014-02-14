package org.pfyu.testfix.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ZipDownloader extends Thread {
	private String url;
	private String dictionary;

	public ZipDownloader(String url, String dictionary) {
		this.url = url;
		this.dictionary = dictionary;
	}

	public void run() {
		try {
			int end = url.lastIndexOf("/");
			String fileName = url.substring(end + 1, url.length());
			File file = new File(dictionary + File.separator + fileName);
			OutputStream os = new FileOutputStream(file);

			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			conn.setDoOutput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			int i = 0;
			byte[] buf = new byte[4096];
			while ((i = is.read(buf)) != -1) {
				os.write(buf, 0, i);
			}
			os.flush();
			os.close();
			is.close();
			System.out.println("******Saved a new testfix <"+fileName+"> into disk");
		} catch (IOException e) {
			System.out.println("failed to download " + url);
		}

	}
}
