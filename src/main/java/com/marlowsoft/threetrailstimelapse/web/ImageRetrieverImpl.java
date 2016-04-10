package com.marlowsoft.threetrailstimelapse.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Concrete implementation of {@link ImageRetriever}. Retrieves an actual image from the web.
 */
public class ImageRetrieverImpl implements ImageRetriever{
    public BufferedImage getImage(final String url) throws IOException {
        return ImageIO.read(new URL(url));
    }
}
