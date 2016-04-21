package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertEquals;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.marlowsoft.threetrailstimelapse.bind.ConcreteModule;
import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Retrieves actual images and webpages from the internet.
 * Slow and prone to failure, especially if you're disconnected from the network.
 */
@Ignore
public class NotFakeTests {
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
        final LocalDate beginDate = new LocalDate(2016, 4, 16);
        final LocalDate endDate = new LocalDate(2016, 4, 19);
        final LocalTime timeOfDay = new LocalTime(12, 0);

        final List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay);

        assertEquals(4, images.size());
    }
}
