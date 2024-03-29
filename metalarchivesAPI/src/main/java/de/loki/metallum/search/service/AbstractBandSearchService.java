package de.loki.metallum.search.service;

import java.util.concurrent.ExecutionException;

import de.loki.metallum.core.parser.site.BandSiteParser;
import de.loki.metallum.entity.Band;
import de.loki.metallum.search.AbstractSearchService;

public abstract class AbstractBandSearchService extends AbstractSearchService<Band> {

	protected boolean	loadImages		= false;
	protected boolean	loadReviews		= false;
	protected boolean	loadSimilar		= false;
	protected boolean	loadLinks		= false;
	protected boolean	loadReadMore	= false;

	public AbstractBandSearchService(final int objectToLoad, final boolean loadImages, final boolean loadReviews, final boolean loadSimilar, final boolean loadReadMore) {
		this.objectToLoad = objectToLoad;
		this.loadImages = loadImages;
		this.loadReviews = loadReviews;
		this.loadSimilar = loadSimilar;
		this.loadReadMore = loadReadMore;
	}

	public final void setLoadReadMore(final boolean loadReadMore) {
		this.loadReadMore = loadReadMore;
	}

	public final void setLoadImages(final boolean loadImages) {
		this.loadImages = loadImages;
	}

	public final void setLoadReviews(final boolean loadReviews) {
		this.loadReviews = loadReviews;
	}

	public final void setLoadSimilar(final boolean loadSimilar) {
		this.loadSimilar = loadSimilar;
	}

	public final void setLoadLinks(final boolean loadLinks) {
		this.loadLinks = loadLinks;
	}

	@Override
	protected final boolean hasAllInformation(final Band bandCache) {
		if (this.loadImages && !bandCache.hasPhoto() && !bandCache.hasLogo()) {
			return false;
		}
		if (this.loadReviews && bandCache.getReviews().isEmpty()) {
			return false;
		}

		if (this.loadSimilar && bandCache.getSimilarArtists().isEmpty()) {
			return false;
		}

		if (this.loadLinks && bandCache.getLinks().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	protected final BandSiteParser getSiteParser(final Band band) throws ExecutionException {
		return new BandSiteParser(band, this.loadImages, this.loadReviews, this.loadSimilar, this.loadLinks, this.loadReadMore);
	}

	@Override
	public void setObjectsToLoad(final int objectToLoad) {
		super.setObjectsToLoad(objectToLoad);
	}
}
