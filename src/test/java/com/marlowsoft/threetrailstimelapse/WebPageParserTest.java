package com.marlowsoft.threetrailstimelapse;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;
import org.joda.time.LocalTime;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link WebPageParser}.
 */
public class WebPageParserTest {
    /**
     * Verifies the times are retrieved properly in {@link WebPageParser#getTimes(Document)}.
     * @throws IOException
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
}
