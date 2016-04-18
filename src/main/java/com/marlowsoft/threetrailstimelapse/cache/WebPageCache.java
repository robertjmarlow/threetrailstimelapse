package com.marlowsoft.threetrailstimelapse.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A cache of a bunch of {@link org.jsoup.nodes.Document}.
 */
public final class WebPageCache {
    private static final LoadingCache<String, Document> webPages;

    static {
        webPages = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Document>() {
                @Override
                public Document load(final String url) throws Exception {
                    return InjectorRetriever.getInjector().getInstance(WebPageRetriever.class).getWebPage(url);
                }
            });
    }

    /**
     * Private constructor to prevent instantiation
     */
    private WebPageCache() {}

    /**
     * Get a web page, from the cache if able. If the web page is not in the cache, get it from the internet.
     * @param url The url of the web page.
     * @return The web page.
     * @throws ExecutionException If something bad happens during the retrieval of the web page.
     */
    public static Document getWebPage(final String url) throws ExecutionException {
        return webPages.get(url);
    }
}
