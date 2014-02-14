package org.pfyu.testfix.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

public class TestFixScanner extends Thread {
	private File root;
	public static File dummy;
	private BlockingQueue<File> zipFileQueue;

	public TestFixScanner(File root, BlockingQueue<File> zipFileQueue) {
		this.root = root;
		dummy = new File("");
		this.zipFileQueue = zipFileQueue;
	}

	public void run() {
		scanFile(root);
		try {
			zipFileQueue.put(dummy);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void scanFile(File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles(new FileFilter() {
				public boolean accept(File arg0) {
					return arg0.isDirectory() || arg0.getName().endsWith(".zip");
				}
			});

			for (File f : list) {
				scanFile(f);
			}

		} else {
			try {
				zipFileQueue.put(file);
				System.out.println("put in queue: " + file.getPath());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
