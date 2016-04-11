package com.marlowsoft.threetrailstimelapse;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link CampusImageRetriever}.
 */
public class CampusImageRetrieverTest {
    @Test
    public void testGetDay() throws IOException {
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate day = new LocalDate(2016, 4, 7);

        final List<BufferedImage> images = retriever.getDay(day);

        assertEquals(5, images.size());
    }
}
