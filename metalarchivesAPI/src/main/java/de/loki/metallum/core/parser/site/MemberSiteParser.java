package de.loki.metallum.core.parser.site;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;

import de.loki.metallum.core.parser.site.helper.LinkParser;
import de.loki.metallum.core.parser.site.helper.member.BandParser;
import de.loki.metallum.core.util.MetallumUtil;
import de.loki.metallum.core.util.net.MetallumURL;
import de.loki.metallum.core.util.net.downloader.Downloader;
import de.loki.metallum.entity.Band;
import de.loki.metallum.entity.Disc;
import de.loki.metallum.entity.Link;
import de.loki.metallum.entity.Member;
import de.loki.metallum.enums.Country;

public class MemberSiteParser extends AbstractSiteParser<Member> {

	private final boolean	loadReadMore;

	public MemberSiteParser(final Member member, final boolean loadImages, final boolean loadMemberLinks, final boolean loadReadMore) throws ExecutionException {
		super(member, loadImages, loadMemberLinks);
		this.loadReadMore = loadReadMore;
	}

	@Override
	public final Member parse() {
		Member member = new Member(this.entity.getId());
		member.setName(parseName());
		member.setRealName(parseRealName());
		member.setAge(parseAge());
		member.setCountry(parseCountry());
		member.setProvince(parseProvince());
		member.setGender(parseGender());
		final String imageUrl = parseImageUrl();
		member.setPhotoUrl(imageUrl);
		member.setPhoto(parseImage(imageUrl));
		member.setGuestIn(parseGuestSessionBands());
		member.setActiveIn(parseActiveBands());
		member.setPastBands(parsePastBands());
		member.setMiscActivitys(parseMiscBands());
		member.setDetails(parseDetails());
		member.addLinks(parseLinks());
		member = parseModfications(member);
		return member;
	}

	private final String parseName() {
		String name = this.html.substring(this.html.indexOf("<h1 class=\"band_member_name\">") + 29, this.html.indexOf("</h1>"));
		return name;
	}

	private final String parseRealName() {
		String[] discDetails = this.html.substring(this.html.indexOf("<dl class=\"float_left\"")).split("<dd>");
		String realName = discDetails[1];
		realName = realName.substring(0, realName.indexOf("</dd>"));
		return realName;
	}

	private final int parseAge() {
		final String[] discDetails = this.html.substring(this.html.indexOf("<dl class=\"float_left\"")).split("<dd>");
		String age = discDetails[2];
		if (age.contains("N/A")) {
			return 0;
		} else {
			age = age.substring(0, age.indexOf("</dd>")).trim();
			if (age.contains("(born")) {
				age = age.replaceAll("\\(born.*", "").trim();
			}
			return Integer.parseInt(age);
		}
	}

	private final Country parseCountry() {
		final String details = this.html.substring(this.html.indexOf("<dl class=\"float_right\""));
		final String[] memberDetails = details.split("<dd>");
		if (memberDetails[1].contains("N/A")) {
			return Country.ANY;
		}
		final String country = memberDetails[1].substring(memberDetails[1].indexOf("\">") + 2, memberDetails[1].indexOf("</a>"));
		return Country.getRightCountryForString(country);
	}

	private String parseProvince() {
		final String details = this.html.substring(this.html.indexOf("<dl class=\"float_right\""));
		final String[] memberDetails = details.split("<dd>");
		if (Jsoup.parse(memberDetails[1]).text().matches(".*\\(.+\\).*")) {
			String province = Jsoup.parse(memberDetails[1]).text();
			province = province.substring(province.indexOf(" (") + 2, province.indexOf(") "));
			return province;
		}
		return "";
	}

	private final String parseGender() {
		String details = this.html.substring(this.html.indexOf("<dl class=\"float_right\""));
		String[] memberDetails = details.split("<dd>");
		String gender = memberDetails[2].substring(0, memberDetails[2].indexOf("</dd>"));
		return gender;
	}

	private Map<Band, Map<Disc, String>> parseActiveBands() {
		final BandParser parser = new BandParser();
		return parser.parse(this.html, BandParser.Mode.ACTIVE);
	}

	private Map<Band, Map<Disc, String>> parsePastBands() {
		final BandParser parser = new BandParser();
		return parser.parse(this.html, BandParser.Mode.PAST);
	}

	private Map<Band, Map<Disc, String>> parseGuestSessionBands() {
		final BandParser parser = new BandParser();
		return parser.parse(this.html, BandParser.Mode.GUEST);
	}

	// 2007 Equally Destructive (Single) Design, above the band name
	private Map<Band, Map<Disc, String>> parseMiscBands() {
		final BandParser parser = new BandParser();
		return parser.parse(this.html, BandParser.Mode.MISC);
	}

	private Link[] parseLinks() {
		final List<Link> linksFromEntity = this.entity.getLinks();
		if (!linksFromEntity.isEmpty()) {
			final Link[] linkArray = new Link[linksFromEntity.size()];
			linksFromEntity.toArray(linkArray);
			return linkArray;
		} else if (this.loadLinks) {
			try {
				final LinkParser parser = new LinkParser(this.entity.getId(), LinkParser.MEMBER_PARSER);
				return parser.parse();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return new Link[0];
	}

	private final String parseImageUrl() {
		String imageUrl = null;
		if (this.html.contains("<div class=\"member_img\">")) {
			imageUrl = this.html.substring(this.html.indexOf("<div class=\"member_img\""));
			imageUrl = imageUrl.substring(imageUrl.indexOf("href=\"") + 6);
			imageUrl = imageUrl.substring(0, imageUrl.indexOf("\""));
		}
		return imageUrl;

	}

	private final BufferedImage parseImage(final String imageUrl) {

		BufferedImage imagePhoto = this.entity.getPhoto();
		if (imagePhoto != null) {
			return imagePhoto;
		}
		if (this.loadImage && imageUrl != null) {
			try {
				imagePhoto = Downloader.getImage(imageUrl);
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}
		return imagePhoto;
	}

	private String parseReadMore() {
		String html = "";
		try {
			html = Downloader.getHTML(MetallumURL.assembleMemberReadMoreURL(this.entity.getId()));
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}
		return MetallumUtil.parseHtmlWithLineSeperators(html);
	}

	private final String parseDetails() {
		if (this.loadReadMore && this.html.contains("class=\"btn_read_more")) {
			return parseReadMore();
		}
		String biography = this.html.substring(this.html.indexOf("<div class=\"clear band_comment\">"));
		biography = biography.substring(0, biography.indexOf("</div>"));
		// To keep the headlines formatted
		biography = biography.replaceAll("</?h.*?>", "<br><br>");
		biography = MetallumUtil.parseHtmlWithLineSeperators(biography);
		return biography;
	}

	@Override
	protected final String getSiteURL() {
		return MetallumURL.assembleMemberURL(this.entity.getId());
	}

}
