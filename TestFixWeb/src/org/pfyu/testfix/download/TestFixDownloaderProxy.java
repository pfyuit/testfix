package org.pfyu.testfix.download;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.pfyu.testfix.data.html.TR;

public class TestFixDownloaderProxy implements IDownloader {
	
	IDownloader downloader;
	
	public TestFixDownloaderProxy(IDownloader downloader){
		this.downloader = downloader;
	}

	@Override
	public List<String> downloadHTML(URL url) throws IOException, InterruptedException {
		return downloader.downloadHTML(url);
	}

	@Override
	public List<TR> parseHTML(List<String> html) {
		return downloader.parseHTML(html);
	}

	@Override
	public void downloadZip(List<TR> trList, String outputDic) {
		downloader.downloadZip(trList, outputDic);
	}

}
