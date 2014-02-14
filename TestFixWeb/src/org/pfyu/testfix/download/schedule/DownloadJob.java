package org.pfyu.testfix.download.schedule;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.pfyu.testfix.data.html.TR;
import org.pfyu.testfix.download.IDownloader;
import org.pfyu.testfix.download.TestFixDownloader;
import org.pfyu.testfix.download.TestFixDownloaderProxy;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DownloadJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("**********download job is running**************");

		IDownloader proxy = new TestFixDownloaderProxy(new TestFixDownloader());
		List<String> html = null;
		try {
			System.out.println("**********1. download the html from fix portal**************");
			html = proxy.downloadHTML(new URL("http://rcs-testfix.ratl.swg.usma.ibm.com/cgi-bin/fix_portal.pl"));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("**********1. download the html from fix portal finished**************");

		System.out.println("**********2. parse the downloaded html**************");
		List<TR> trList = proxy.parseHTML(html);
		System.out.println("**********2. parse the downloaded html finished**************");

		System.out.println("**********3. assign each html row to a thread to download**************");
		proxy.downloadZip(trList, "C:\\TestFixData\\testfixoutput");
	}

}
