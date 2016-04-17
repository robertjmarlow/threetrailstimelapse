package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertEquals;

import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Tests {@link CampusImageRetriever}.
 */
public class CampusImageRetrieverTest {
    /**
     * Gets a single day and verifies the number of retrieved images is correct.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testGetDay() throws IOException, InterruptedException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate day = new LocalDate(2016, 4, 7);

        final List<BufferedImage> images = retriever.getDay(day);

        assertEquals(31, images.size());
    }

    /**
     * Get a date range and verifies the number of retrieved images is correct.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testGetDateRange() throws IOException, InterruptedException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = new LocalDate(2016, 1, 1);
        final LocalDate endDate = new LocalDate(2016, 4, 15);
        final LocalTime timeOfDay = new LocalTime(12, 0);

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay);

        assertEquals(106, images.size());
    }
}
