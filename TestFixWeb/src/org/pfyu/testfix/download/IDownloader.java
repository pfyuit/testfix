package org.pfyu.testfix.download;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.pfyu.testfix.data.html.TR;

public interface IDownloader {

	public abstract List<String> downloadHTML(URL url) throws IOException, InterruptedException;

	public abstract List<TR> parseHTML(List<String> html);

	public abstract void downloadZip(List<TR> trList, String outputDic);

}