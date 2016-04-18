package com.marlowsoft.threetrailstimelapse.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A cache of a bunch of {@link java.awt.image.BufferedImage}.
 */
public final class ImageCache {
    private static final LoadingCache<String, BufferedImage> images;

    static {
        images = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, BufferedImage>() {
                @Override
                public BufferedImage load(final String url) throws Exception {
                    return InjectorRetriever.getInjector().getInstance(ImageRetriever.class).getImage(url);
                }
            });
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ImageCache() {}

    /**
     * Get an image, from the cache if able. If the image is not in the cache, get it from the internet.
     * @param url The url of the image.
     * @return The image.
     * @throws ExecutionException If something bad happens during the retrieval of the image.
     */
    public static BufferedImage getImage(final String url) throws ExecutionException {
        return images.get(url);
    }
}
