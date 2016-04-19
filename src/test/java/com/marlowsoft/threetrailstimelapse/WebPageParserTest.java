package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertEquals;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.mock.FakeWebPageRetrieverImpl;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.joda.time.LocalTime;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
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
        assertEquals(6, times.get(0).getHourOfDay());
        assertEquals(59, times.get(0).getMinuteOfHour());
        assertEquals(12, times.get(15).getHourOfDay());
        assertEquals(0, times.get(15).getMinuteOfHour());
    }

    /**
     * Verifies that times that start have the format "00:xyam", e.g. "00:40am",
     * will come back as "12:40am". This is necessary because joda time doesn't
     * like the "00:xyam" format.
     */
    @Test
    public void testGetTimesNearMidnight() throws IOException {
        FakeWebPageRetrieverImpl.setFakePage("webpage-midnight-times.html");

        final Injector injector = Guice.createInjector(new FakeModule());
        final WebPageRetriever webPageRetriever = injector.getInstance(WebPageRetriever.class);

        final List<LocalTime> times = WebPageParser.getTimes(webPageRetriever.getWebPage(""));

        assertEquals(48, times.size());
        assertEquals(0, times.get(0).getHourOfDay());
        assertEquals(0, times.get(0).getMinuteOfHour());
        assertEquals(0, times.get(1).getHourOfDay());
        assertEquals(20, times.get(1).getMinuteOfHour());
        assertEquals(0, times.get(2).getHourOfDay());
        assertEquals(40, times.get(2).getMinuteOfHour());
        assertEquals(1, times.get(3).getHourOfDay());
        assertEquals(0, times.get(3).getMinuteOfHour());
    }

    @After
    public void tearDown() {
        FakeWebPageRetrieverImpl.setFakePage("webpage.html");
    }
}
