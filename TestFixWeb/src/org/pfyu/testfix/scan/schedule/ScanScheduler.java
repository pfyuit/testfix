package org.pfyu.testfix.scan.schedule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.pfyu.testfix.dao.TestFixDAO;
import org.pfyu.testfix.data.entity.Testfix;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ScanScheduler {

	private JobDetail jobDetail;
	private CronTriggerImpl trigger;
	private Scheduler scheduler;
	private static ScanScheduler instance;
	private List<String> existingTestFixNamesInDB = new ArrayList<String>();
	private boolean isRunning;

	private ScanScheduler() {
	}

	public static ScanScheduler getInstance() {
		if (instance == null) {
			instance = new ScanScheduler();
		}
		return instance;
	}
		
	public List<String> getExistingTestFixNamesInDB() {
		if(existingTestFixNamesInDB.isEmpty()){
			ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
			TestFixDAO dao = (TestFixDAO)ctx.getBean("testfixDAO");
			List<Testfix> all = dao.getTestfixs();
			for(Testfix fix : all){
				existingTestFixNamesInDB.add(fix.getTestfixname());
			}	
		}
		return existingTestFixNamesInDB;
	}
	
	public void clear() {
		this.existingTestFixNamesInDB.clear();
	}

	public void start() {
		System.out.println("*************starting scanner at 5:00 AM every day*********");
		try {
			jobDetail = new JobDetailImpl("myscanjob", "myscanjobgroup", ScanJob.class);
			trigger = new CronTriggerImpl("myscantrigger");
			CronExpression expression = new CronExpression("0 0 5 * * ?");
			trigger.setCronExpression(expression);
			
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
			System.out.println("*************scanner started at 5:00 AM every day*********");
			isRunning = true;
		} catch (ParseException | SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void scanNow() {
		System.out.println("*************scanning now*********");
		try {
			ScanJob job = new ScanJob();
			job.execute(null);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		System.out.println("*************stopping scanner****************");
		try {
			scheduler.shutdown();
			System.out.println("*************scanner stopped****************");
			isRunning = false;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

}
