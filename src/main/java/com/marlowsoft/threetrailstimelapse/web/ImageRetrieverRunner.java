package com.marlowsoft.threetrailstimelapse.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * Threaded means to get an image from the web using an implementation of {@link ImageRetriever}.
 */
public class ImageRetrieverRunner implements Runnable {
    private final ImageRetriever imageRetriever;
    private final Map<Integer, BufferedImage> images;
    private final int imageOrder;
    private final String url;

    /**
     * Gets an image from the internet in a threaded manner.
     * @param imageRetriever The means to get the image.
     * @param images A thread-safe collection to store the returned image.
     *               <p>
     *                  Note: No checking is done for thread safety of the collection. The calling client
     *                  is responsible for this.
     *               </p>
     * @param imageOrder The index of the image relative to the time of day.
     * @param url The url of the image to get.
     */
    public ImageRetrieverRunner(final ImageRetriever imageRetriever, final Map<Integer, BufferedImage> images,
                                final int imageOrder, final String url) {
        this.imageRetriever = imageRetriever;
        this.images = images;
        this.imageOrder = imageOrder;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            images.put(imageOrder, imageRetriever.getImage(url));
        } catch (final IOException e) {
            // TODO log4j
            e.printStackTrace();
        }
    }
}
