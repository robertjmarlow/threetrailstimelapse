package com.marlowsoft.threetrailstimelapse.encode;

import org.jcodec.api.awt.SequenceEncoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Encodes images into a webm format.
 */
public final class VideoEncoder {
    /**
     * Private constructor to prevent instantiation.
     */
    private VideoEncoder() {}

    /**
     * Encodes the series of images into a webm video.
     * @param images The series of images to encode.
     * @param fileName The name of the output webm video file.
     * @throws IOException If something goes wrong while encoding.
     */
    public static void encode(final List<BufferedImage> images, final String fileName) throws IOException {
        final SequenceEncoder encoder = new SequenceEncoder(new File(fileName));

        for (final BufferedImage image : images) {
            encoder.encodeImage(image);
        }

        encoder.finish();
    }
}
