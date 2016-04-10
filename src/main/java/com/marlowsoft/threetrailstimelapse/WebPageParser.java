package com.marlowsoft.threetrailstimelapse;

import com.google.common.collect.ImmutableList;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Methods to parse the web page the campus images are stored on.
 */
final class WebPageParser {
    /**
     * Private constructor to prevent instantiation
     */
    private WebPageParser() {}

    private static final String TIME_FORMAT = "hh:mma";
    private static final DateTimeFormatter timeFormat = DateTimeFormat.forPattern(TIME_FORMAT);

    /**
     * Get all the times from the web page.
     * @param doc The web page the times are on.
     * @return An immutable list of all times listed on the web page.
     */
    static List<LocalTime> getTimes(final Document doc) {
        final ImmutableList.Builder<LocalTime> times = ImmutableList.builder();
        final Elements timeElements = doc.select("#pickTime div");

        for (final Element timeElement : timeElements) {
            times.add(timeFormat.parseLocalTime(timeElement.text()));
        }

        return times.build();
    }
}
