package com.marlowsoft.threetrailstimelapse.web;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Defines methods to retrieve an image from the internet.
 */
public interface ImageRetriever {
    /**
     * Gets an image from the internet.
     * @param url The image on the internet to retrieve.
     * @return The image from the internet.
     * @throws IOException If something goes wrong while getting the image.
     */
    BufferedImage getImage(String url) throws IOException;
}
