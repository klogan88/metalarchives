package de.loki.metallum.core.parser.site.helper.band;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;

import de.loki.metallum.core.util.net.MetallumURL;
import de.loki.metallum.core.util.net.downloader.Downloader;
import de.loki.metallum.entity.Band;
import de.loki.metallum.enums.Country;

public class SimilarArtistsParser {
	private final String	html;

	public SimilarArtistsParser(final long id) throws ExecutionException {
		this.html = Downloader.getHTML(MetallumURL.assembleBandRecommendationsURL(id, 1));
	}

	public Map<Integer, List<Band>> parse() {
		final Map<Integer, List<Band>> returnMap = new TreeMap<Integer, List<Band>>();
		final String[] bandStringArray = this.html.split("<tr id=\"recRow_");
		for (int i = 1; i < bandStringArray.length; i++) {
			final String[] bandInformationStringArray = bandStringArray[i].split("<td>");
			int index = 1;
			final Band band = new Band(parseBandId(bandInformationStringArray[index]));
			band.setName(parseBandName(bandInformationStringArray[index++]));
			band.setCountry(parseCountry(bandInformationStringArray[index++]));
			band.setGenre(parseGenre(bandInformationStringArray[index++]));
			addToMap(returnMap, parseScore(bandInformationStringArray[index]), band);
		}
		return returnMap;
	}

	private static Map<Integer, List<Band>> addToMap(final Map<Integer, List<Band>> themap, final int key, final Band value) {
		List<Band> bandListFromMap = themap.get(key);
		if (bandListFromMap == null) {
			bandListFromMap = new ArrayList<Band>();
			bandListFromMap.add(value);
		} else {
			bandListFromMap.add(value);
		}
		themap.put(key, bandListFromMap);
		return themap;
	}

	private long parseBandId(final String htmlPart) {
		String strId = htmlPart.substring(0, htmlPart.lastIndexOf("\">"));
		strId = strId.substring(strId.lastIndexOf("/") + 1, strId.length());
		return Long.parseLong(strId);
	}

	private String parseBandName(final String htmlPart) {
		String bandName = Jsoup.parse(htmlPart).text();
		return bandName;
	}

	private Country parseCountry(final String htmlPart) {
		String strCounty = Jsoup.parse(htmlPart).text();
		return Country.getRightCountryForString(strCounty);
	}

	private String parseGenre(final String htmlPart) {
		String genre = Jsoup.parse(htmlPart).text();
		return genre;
	}

	private int parseScore(final String htmlPart) {
		String strScore = Jsoup.parse(htmlPart).text();
		if (strScore.length() > 3) {
			strScore = strScore.substring(0, strScore.indexOf(" "));
		}
		return Integer.parseInt(strScore);
	}
}
