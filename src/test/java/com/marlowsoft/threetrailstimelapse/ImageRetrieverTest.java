package com.marlowsoft.threetrailstimelapse;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.marlowsoft.threetrailstimelapse.bind.ConcreteModule;
import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link com.marlowsoft.threetrailstimelapse.web.ImageRetrieverImpl}.
 */
public class ImageRetrieverTest {
    @Test
    public void getFakeImage() throws IOException {
        final Injector injector = Guice.createInjector(new FakeModule());
        final ImageRetriever imageRetriever = injector.getInstance(ImageRetriever.class);

        final BufferedImage image = imageRetriever.getImage("");

        assertEquals(600, image.getWidth());
        assertEquals(607, image.getHeight());
    }

    @Test
    public void getFakePage() throws IOException {
        final Injector injector = Guice.createInjector(new FakeModule());
        final WebPageRetriever webPageRetriever = injector.getInstance(WebPageRetriever.class);

        final String imgUrl = "pCAMovie/CERNERNEimage16-04-07_12-00-01-73.JPG";

        final Document doc = webPageRetriever.getWebPage("webpage.html");
        final Elements image = doc.select(".image img");

        assertEquals(imgUrl, image.attr("src"));
    }

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
}
