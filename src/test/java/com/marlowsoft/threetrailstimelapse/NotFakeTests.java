package com.marlowsoft.threetrailstimelapse;

import static com.marlowsoft.threetrailstimelapse.TestConstants.REDIS_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.marlowsoft.threetrailstimelapse.bind.ConcreteModule;
import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.encode.VideoEncoder;
import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
 * Retrieves actual images and webpages from the internet.
 * Slow and prone to failure, especially if you're disconnected from the network.
 */
@Ignore
public class NotFakeTests {
    @BeforeClass
    public static void suiteSetUp() {
        Settings.setRedisSettings(new RedisSettings(true, REDIS_PORT, null));
    }

    /**
     * Grabs an actual image from the actual web site.
     * @throws IOException If something bad happens when interacting with the web.
     */
    @Test
    public void getPageAndImage() throws IOException {
        final Injector injector = Guice.createInjector(new ConcreteModule());
        final ImageRetriever imageRetriever = injector.getInstance(ImageRetriever.class);
        final WebPageRetriever webPageRetriever = injector.getInstance(WebPageRetriever.class);

        final String urlBase = "http://p-tn.net/pCAM/CERNERNE/";
        final String imgUrl = "pCAMovie/CERNERNEimage16-04-07_12-00-01-73.JPG";
        final String pageUrl = urlBase + "archivepics.asp?m=4&d=7&y=2016&h=12&min=0&scrollPos=0";

        final Document doc = webPageRetriever.getWebPage(pageUrl);
        final Elements imageElem = doc.select(".image img");

        assertEquals(imgUrl, imageElem.attr("src"));

        final BufferedImage image = imageRetriever.getImage(urlBase + imageElem.attr("src"));

        assertEquals(1280, image.getWidth());
        assertEquals(720, image.getHeight());
    }

    /**
     * Gets a few real pages and images from the internet.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void getAFewPagesAndImages() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new ConcreteModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 4, 16);
        final LocalDate endDate = LocalDate.of(2016, 4, 19);
        final LocalTime timeOfDay = LocalTime.of(12, 0);

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay);

        assertEquals(4, images.size());
    }

    /**
     * Gets a whole lot of real pages and images from the internet and then encode them to a video.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    public void getALotOfPagesAndImages() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new ConcreteModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 1, 1);
        final LocalDate endDate = LocalDate.of(2016, 5, 1);
        final LocalTime timeOfDay = LocalTime.of(12, 0);
        final Duration fuzziness = Duration.of(60, ChronoUnit.MINUTES);

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay, fuzziness);

        assertEquals(121, images.size());

        VideoEncoder.encode(images, "build/cool.webm");
    }

    /**
     * There are a bunch of images missing on the website in Feb and March of 2016.
     * Test to make sure that the service handles this gracefully.
     * <i>Update:</i> They've fixed this. This test is <i>sort of</i> irrelevant now.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    @Test
    @Ignore
    public void test404Images() throws InterruptedException, ExecutionException, IOException {
        InjectorRetriever.setInjector(new ConcreteModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();

        final LocalDate beginDate = LocalDate.of(2016, 1, 1);
        final LocalDate endDate = LocalDate.of(2016, 5, 1);
        final LocalTime timeOfDay = LocalTime.of(12, 0);
        final Duration fuzziness = Duration.of(60, ChronoUnit.MINUTES);

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay, fuzziness);

        assertEquals(62, images.size());
    }

    /**
     * Test to make sure that a page that doesn't exist will be ignored correctly by
     * throwing an IOException.
     */
    @Test
    public void testNonexistentDate() {
        Settings.setRedisSettings(new RedisSettings(true, REDIS_PORT, null));
        InjectorRetriever.setInjector(new ConcreteModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2015, 1, 1);
        final LocalDate endDate = LocalDate.of(2015, 1, 1);
        final LocalTime timeOfDay = LocalTime.of(12, 0);
        boolean exceptionThrown = false;

        try {
            retriever.getDateRange(beginDate, endDate, timeOfDay);
        } catch (final Exception e) {
            assertTrue(e.getCause() instanceof IOException);
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
}
