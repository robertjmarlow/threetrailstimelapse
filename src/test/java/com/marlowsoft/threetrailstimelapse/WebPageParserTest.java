package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertEquals;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.mock.FakeWebPageRetrieverImpl;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;

/**
 * Tests {@link WebPageParser}.
 */
public class WebPageParserTest {
    /**
     * Verifies the times are retrieved properly in {@link WebPageParser#getTimes(Document)}.
     * @throws IOException Not possible with the mock implementation.
     */
    @Test
    public void testGetTimes() throws IOException {
        final Injector injector = Guice.createInjector(new FakeModule());
        final WebPageRetriever webPageRetriever = injector.getInstance(WebPageRetriever.class);

        final List<LocalTime> times = WebPageParser.getTimes(webPageRetriever.getWebPage("webpage.html"));

        assertEquals(31, times.size());
        assertEquals(6, times.get(0).getHour());
        assertEquals(59, times.get(0).get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(12, times.get(15).getHour());
        assertEquals(0, times.get(15).get(ChronoField.MINUTE_OF_HOUR));
    }

    /**
     * Verifies that times that start have the format "00:xyam", e.g. "00:40am",
     * will come back as "12:40am". This is necessary because joda time doesn't
     * like the "00:xyam" format.
     * @throws IOException Not possible with the mock implementation.
     */
    @Test
    public void testGetTimesNearMidnight() throws IOException {
        FakeWebPageRetrieverImpl.setFakePage("webpage-midnight-times.html");

        final Injector injector = Guice.createInjector(new FakeModule());
        final WebPageRetriever webPageRetriever = injector.getInstance(WebPageRetriever.class);

        final List<LocalTime> times = WebPageParser.getTimes(webPageRetriever.getWebPage(""));

        assertEquals(48, times.size());
        assertEquals(0, times.get(0).getHour());
        assertEquals(0, times.get(0).get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(0, times.get(1).getHour());
        assertEquals(20, times.get(1).get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(0, times.get(2).getHour());
        assertEquals(40, times.get(2).get(ChronoField.MINUTE_OF_HOUR));
        assertEquals(1, times.get(3).getHour());
        assertEquals(0, times.get(3).get(ChronoField.MINUTE_OF_HOUR));
    }

    @After
    public void tearDown() {
        FakeWebPageRetrieverImpl.setFakePage("webpage.html");
    }
}
