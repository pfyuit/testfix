package org.pfyu.testfix.web.actions;

import org.pfyu.testfix.scan.schedule.ScanScheduler;

import com.opensymphony.xwork2.ActionSupport;

public class ScannerAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private String scannerRunningStatus = "Stopped";

	public String getScannerRunningStatus() {
		scannerRunningStatus = ScanScheduler.getInstance().isRunning() ? "Started at 5:00 AM every day" : "Stopped";
		return scannerRunningStatus;
	}
	
	public String startScanner() {
		ScanScheduler.getInstance().start();
		scannerRunningStatus = getScannerRunningStatus();
		return SUCCESS;
	}
	
	public String scanNow() {
		ScanScheduler.getInstance().scanNow();
		return SUCCESS;
	}

	public String stopScanner() {
		ScanScheduler.getInstance().stop();
		scannerRunningStatus = getScannerRunningStatus();
		return SUCCESS;
	}


	@Override
	public String execute() throws Exception {
		System.out.println("execute scanner action...");
		getScannerRunningStatus();
		return super.SUCCESS;
	}

}
