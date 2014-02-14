package org.pfyu.testfix.download.schedule;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

public class DownloadScheduler {

	private JobDetail jobDetail;
	private CronTriggerImpl trigger;
	private Scheduler scheduler;
	private static DownloadScheduler instance;
	private List<String> existingTestFixNamesInDisk = new ArrayList<String>();
	private boolean isRunning;

	private DownloadScheduler() {
	}

	public static DownloadScheduler getInstance() {
		if (instance == null) {
			instance = new DownloadScheduler();
		}
		return instance;
	}

	public List<String> getExistingTestFixNamesInDisk() {
		if (existingTestFixNamesInDisk.isEmpty()) {
			File file = new File("C:\\TestFixData\\testfixoutput");
			if (file.isDirectory()) {
				File[] list = file.listFiles(new FileFilter() {
					public boolean accept(File arg0) {
						return arg0.isDirectory() || arg0.getName().endsWith(".zip");
					}
				});

				for (File f : list) {
					existingTestFixNamesInDisk.add(f.getName().substring(0, f.getName().length()-4));
				}

			}
		}
		return existingTestFixNamesInDisk;
	}

	public void clear() {
		this.existingTestFixNamesInDisk.clear();
	}

	public void start() {
		System.out.println("*************starting downloader at 4:00 AM every day*********");
		try {
			jobDetail = new JobDetailImpl("mydownloadjob", "mydownloadjobgroup", DownloadJob.class);
			trigger = new CronTriggerImpl("mydownloadtrigger");
			CronExpression expression = new CronExpression("0 0 4 * * ?");
			trigger.setCronExpression(expression);

			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
			System.out.println("*************downloader started at 4:00 AM every day*********");
			isRunning = true;
		} catch (ParseException | SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadNow() {
		System.out.println("*************downloading now*********");
		try {
			DownloadJob job = new DownloadJob();
			job.execute(null);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		System.out.println("*************stopping downloader****************");
		try {
			scheduler.shutdown();
			System.out.println("*************downloader stopped****************");
			isRunning = false;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

}
