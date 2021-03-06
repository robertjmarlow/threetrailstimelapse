package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertEquals;

import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.mock.FakeWebPageRetrieverImpl;

import org.junit.After;
import org.junit.Test;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Tests {@link CampusImageRetriever}.
 */
public class CampusImageRetrieverTest {
    /**
     * Gets a single day and verifies the number of retrieved images is correct.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     */
    @Test
    public void testGetDay() throws IOException, InterruptedException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate day = LocalDate.of(2016, 4, 7);

        final List<BufferedImage> images = retriever.getDay(day);

        assertEquals(31, images.size());
    }

    /**
     * Get a date range and verifies the number of retrieved images is correct.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void testGetDateRange() throws IOException, InterruptedException, ExecutionException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 1, 1);
        final LocalDate endDate = LocalDate.of(2016, 4, 15);
        final LocalTime timeOfDay = LocalTime.of(12, 0);

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay);

        assertEquals(106, images.size());
    }

    /**
     * Tests to make sure that images aren't added if there isn't an image there for that time of the day.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void testGetDateRangeNoTimeOfDay() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 4, 16);
        final LocalDate endDate = LocalDate.of(2016, 4, 19);
        final LocalTime timeOfDay = LocalTime.of(12, 0);

        FakeWebPageRetrieverImpl.setFakePage("webpage-midnight-times.html");

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay);

        assertEquals(0, images.size());
    }

    /**
     * Tests to make sure that an image is retrieved when using the <code>fuzziness</code> parameter in
     * {@link CampusImageRetriever#getDateRange(LocalDate, LocalDate, LocalTime, Duration)}.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void testFuzzyDateRange() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 4, 16);
        final LocalDate endDate = LocalDate.of(2016, 4, 16);
        final LocalTime timeOfDay = LocalTime.of(12, 39);
        final Duration fuzziness = Duration.of(40, ChronoUnit.MINUTES);

        // luckily, this has a gap around noon
        FakeWebPageRetrieverImpl.setFakePage("webpage-midnight-times.html");

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay, fuzziness);

        assertEquals(1, images.size());
    }

    /**
     * Verifies that an image is retrieved if a time is actually found and no search needs to be done.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void testFuzzyDateRangeNoFuzziness() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 4, 16);
        final LocalDate endDate = LocalDate.of(2016, 4, 16);
        final LocalTime timeOfDay = LocalTime.of(12, 20);
        final Duration fuzziness = Duration.of(0, ChronoUnit.MINUTES);

        FakeWebPageRetrieverImpl.setFakePage("webpage-midnight-times.html");

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay, fuzziness);

        assertEquals(1, images.size());
    }

    /**
     * Verifies no images are retrieved if the fuzziness isn't enough to find an image.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void testFuzzyDateRangeNoDate() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 4, 16);
        final LocalDate endDate = LocalDate.of(2016, 4, 16);
        final LocalTime timeOfDay = LocalTime.of(11, 30);
        final Duration fuzziness = Duration.of(10, ChronoUnit.MINUTES);

        FakeWebPageRetrieverImpl.setFakePage("webpage-midnight-times.html");

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay, fuzziness);

        assertEquals(0, images.size());
    }

    @After
    public void tearDown() {
        FakeWebPageRetrieverImpl.setFakePage("webpage.html");
    }
}
