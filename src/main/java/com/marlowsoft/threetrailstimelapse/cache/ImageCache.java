package com.marlowsoft.threetrailstimelapse.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A cache of a bunch of {@link java.awt.image.BufferedImage}.
 */
@Singleton
public final class ImageCache {
    private final LoadingCache<String, BufferedImage> images;

    @Inject
    private ImageRetriever imageRetriever;

    /**
     * Private constructor to prevent instantiation
     */
    @Inject
    private ImageCache() {
        images = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, BufferedImage>() {
                    @Override
                    public BufferedImage load(final String url) throws Exception {
                        return imageRetriever.getImage(url);
                    }
                });
    }

    /**
     * Get an image, from the cache if able. If the image is not in the cache, get it from the internet.
     * @param url The url of the image.
     * @return The image.
     * @throws ExecutionException If something bad happens during the retrieval of the image.
     */
    public BufferedImage getImage(final String url) throws ExecutionException {
        return images.get(url);
    }
}
