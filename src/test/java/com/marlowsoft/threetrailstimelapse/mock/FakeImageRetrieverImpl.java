package com.marlowsoft.threetrailstimelapse.mock;

import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Always returns an image taken from the resources directory.
 */
public class FakeImageRetrieverImpl implements ImageRetriever {
    private final static String FILENAME = "regular_expressions.png";

    @Override
    public BufferedImage getImage(String url) throws IOException {
        return ImageIO.read(new File("src/test/resources/" + FILENAME));
    }
}
