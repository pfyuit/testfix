package org.pfyu.testfix.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.pfyu.testfix.dao.TestFixDAO;
import org.pfyu.testfix.scan.schedule.ScanJob;
import org.pfyu.testfix.scan.schedule.ScanScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFixReader extends Thread {

	private BlockingQueue<File> zipFileQueue;
	private Map<String, List<String>> createdOnReleasesCache;
	private Map<String, String> submittedByCache;
	private Map<String, String> pmrCache;
	private Map<String, String> aparCache;
	private Map<String, String> customerCache;
	private Map<String, String> descriptionCache;

	public TestFixReader(BlockingQueue<File> zipFileQueue, Map<String, List<String>> createdOnReleasesCache, Map<String, String> submittedByCache, Map<String, String> pmrCache,
			Map<String, String> aparCache, Map<String, String> customerCache, Map<String, String> descriptionCache) {
		this.zipFileQueue = zipFileQueue;
		this.createdOnReleasesCache = createdOnReleasesCache;
		this.submittedByCache = submittedByCache;
		this.pmrCache = pmrCache;
		this.aparCache = aparCache;
		this.customerCache = customerCache;
		this.descriptionCache = descriptionCache;
	}

	public void run() {
		// while (true) {
		try {
			File file = zipFileQueue.take();
			System.out.println("Take from queue: " + file.getPath());
			if (file == TestFixScanner.dummy) {
				zipFileQueue.put(file);
				System.out.println("dummy file, terminating...");
				return;
			}
			readFile(file);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// }
	}

	private void readFile(File file) throws IOException {
		System.out.println("begin read :" + file.getPath());
		FileInputStream fis = new FileInputStream(file);
		ZipInputStream zis = new ZipInputStream(fis);

		String testFixName = null;
		String aparNameInZip = null;
		List<String> classes = new ArrayList<String>();

		int end = file.getName().lastIndexOf(".zip");
		testFixName = file.getName().substring(0, end);

		// If database already contains this testfix, skip it.
		if (ScanScheduler.getInstance().getExistingTestFixNamesInDB().contains(testFixName)) {
			System.out.println("database already contains this testfix, skip");
			return;
		}

		while (true) {
			ZipEntry entry = zis.getNextEntry();
			if (entry == null) {
				break;
			}
			if (entry.getName().contains("apars")) {
				int startIndex = entry.getName().lastIndexOf("/");
				int endIndex = entry.getName().length();
				aparNameInZip = entry.getName().substring(startIndex + 1, endIndex);
			}
			if (entry.getName().contains(".class") || entry.getName().contains(".js") || entry.getName().contains(".properties") || entry.getName().endsWith(".egl")
					|| entry.getName().contains(".xml")) {
				int startIndex = entry.getName().lastIndexOf("/");
				int endIndex = entry.getName().length();
				String className = entry.getName().substring(startIndex + 1, endIndex);
				classes.add(className);
			}
			zis.closeEntry();
		}

		// Insert into database
		System.out.println("begin insert into DB " + testFixName);
		org.pfyu.testfix.data.entity.Testfix tf = new org.pfyu.testfix.data.entity.Testfix();
		tf.setTestfixname(testFixName);

		String aparName = aparNameInZip;
		String aparInExtraInfo = aparCache.get(testFixName);
		if (aparNameInZip != null && !aparNameInZip.equals(aparInExtraInfo)) {
			tf.setNotes("Mismatched items: APAR name in the ZIP file is " + aparNameInZip + ",\r\n but APAR name in the Fix Portal/Customer Notified is " + aparInExtraInfo+", use the former one");
		} else if (aparNameInZip == null || aparNameInZip.equalsIgnoreCase("null")) {
			aparName = aparInExtraInfo;
			if (aparInExtraInfo == null || aparInExtraInfo.equalsIgnoreCase("null")) {
				tf.setNotes("Both APAR name in ZIP file and in Fix Portal/Customer Notified are null");
			} else {
				tf.setNotes("Mismatched items: APAR name in the ZIP file is null,\r\n but APAR name in the Fix Portal/Customer Notified is " + aparInExtraInfo+", use the later one");
			}
		}
		tf.setAparname(aparName);

		StringBuilder builder = new StringBuilder();
		for (String str : classes) {
			builder.append(str).append("\r\n");
		}
		String classStr = builder.toString();
		if(classStr.endsWith(",")){
			classStr = classStr.substring(0, classStr.length() -1);
		}
		StringBuilder builder1 = new StringBuilder();
		for (String s : createdOnReleasesCache.get(testFixName)) {
			builder1.append(s).append("\r\n");
		}
		tf.setModifiedclass(classStr);
		tf.setCreatedon(builder1.toString());
		tf.setDeliveredin(null);
		String submittedBy = submittedByCache.get(testFixName);
		if (submittedBy != null) {
			int index = submittedBy.indexOf("on");
			submittedBy = submittedBy.substring(0, index).trim();
		}
		tf.setSentby(submittedBy);

		String customer = customerCache.get(testFixName);
		tf.setCustomer(customer);

		String description = descriptionCache.get(testFixName);
		tf.setDescription(description);

		String pmr = pmrCache.get(testFixName);
		tf.setPmr(pmr);

		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		TestFixDAO dao = (TestFixDAO) ctx.getBean("testfixDAO");
		try {
			dao.createTestfix(tf);
			System.out.println("******Inserted a new testfix <" + testFixName + "> into database");
			ScanJob.testFixMsg.put(testFixName, tf);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
