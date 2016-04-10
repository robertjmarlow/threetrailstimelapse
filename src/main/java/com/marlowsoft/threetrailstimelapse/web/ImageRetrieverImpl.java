package com.marlowsoft.threetrailstimelapse.web;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Concrete implementation of {@link ImageRetriever}. Retrieves an actual image from the web.
 */
public class ImageRetrieverImpl implements ImageRetriever{
    public BufferedImage getImage(final String url) throws IOException {
        return ImageIO.read(new URL(url));
    }
}
