package de.loki.metallum.core.util.net.downloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.loki.metallum.core.util.net.downloader.interfaces.IHTMLDowloader;

public final class HTMLDownloader extends AbstractDownloader implements IHTMLDowloader {

	protected HTMLDownloader(final String urlString) {
		super(urlString);
	}

	@Override
	public final String call() throws Exception {
		InputStream in = null;
		BufferedReader rd = null;
		StringBuilder htmlStringBuilder = new StringBuilder();
		try {
			in = getDownloadEntity().getContent();
			rd = new BufferedReader(new InputStreamReader(in, HTML_CHARSET));
			String line;
			while ((line = rd.readLine()) != null) {
				htmlStringBuilder.append(line);
			}
		} finally {
			if (rd != null) {
				rd.close();
			}
			if (in != null) {
				in.close();
			}
		}

		return htmlStringBuilder.toString();
	}

}
