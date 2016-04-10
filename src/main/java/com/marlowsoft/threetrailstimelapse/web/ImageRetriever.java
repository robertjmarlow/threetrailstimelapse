package com.marlowsoft.threetrailstimelapse.web;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Defines methods to retrieve an image from the internet.
 */
public interface ImageRetriever {
    BufferedImage getImage(String url) throws IOException;
}
