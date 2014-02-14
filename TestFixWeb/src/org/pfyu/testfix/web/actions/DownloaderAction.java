package org.pfyu.testfix.web.actions;

import org.pfyu.testfix.download.schedule.DownloadScheduler;

import com.opensymphony.xwork2.ActionSupport;

public class DownloaderAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	private String downloaderRunningStatus = "Stopped";

	public String getDownloaderRunningStatus() {
		downloaderRunningStatus = DownloadScheduler.getInstance().isRunning() ? "Started at 4:00 AM every day" : "Stopped";
		return downloaderRunningStatus;
	}

	public String startDownloader() {
		DownloadScheduler.getInstance().start();
		downloaderRunningStatus = getDownloaderRunningStatus();
		return SUCCESS;
	}
	
	public String downloadNow() {
		DownloadScheduler.getInstance().downloadNow();
		return SUCCESS;
	}

	public String stopDownloader() {
		DownloadScheduler.getInstance().stop();
		downloaderRunningStatus = getDownloaderRunningStatus();
		return SUCCESS;
	}

	@Override
	public String execute() throws Exception {
		System.out.println("execute downloader action...");
		getDownloaderRunningStatus();
		return super.SUCCESS;
	}

}
