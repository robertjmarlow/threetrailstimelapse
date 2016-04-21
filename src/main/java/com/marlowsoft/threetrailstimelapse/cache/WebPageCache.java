package com.marlowsoft.threetrailstimelapse.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A cache of a bunch of {@link org.jsoup.nodes.Document}.
 */
@Singleton
public final class WebPageCache {
    private final LoadingCache<String, Document> webPages;

    @Inject
    private WebPageRetriever webPageRetriever;

    /**
     * Private constructor to prevent instantiation
     */
    @Inject
    private WebPageCache() {
        webPages = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Document>() {
                    @Override
                    public Document load(final String url) throws Exception {
                        return webPageRetriever.getWebPage(url);
                    }
                });
    }

    /**
     * Get a web page, from the cache if able. If the web page is not in the cache, get it from the internet.
     * @param url The url of the web page.
     * @return The web page.
     * @throws ExecutionException If something bad happens during the retrieval of the web page.
     */
    public Document getWebPage(final String url) throws ExecutionException {
        return webPages.get(url);
    }
}
