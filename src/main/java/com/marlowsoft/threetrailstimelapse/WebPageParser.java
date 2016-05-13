package com.marlowsoft.threetrailstimelapse;

import com.google.common.collect.ImmutableList;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Methods to parse the web page the campus images are stored on.
 */
final class WebPageParser {
    /**
     * Private constructor to prevent instantiation
     */
    private WebPageParser() {}

    private static final String TIME_FORMAT = "hh':'mma";
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);

    private static final String BASE_IMAGE_URL = "http://p-tn.net/pCAM/CERNERNE/";

    private static final String TIME_DIVS_SELECTOR = "#pickTime div";
    private static final String WEBCAM_IMAGE_SELECTOR = ".image img";

    /**
     * Get all the times from the web page.
     * @param doc The web page the times are on.
     * @return An immutable list of all times listed on the web page.
     */
    static List<LocalTime> getTimes(final Document doc) {
        final ImmutableList.Builder<LocalTime> times = ImmutableList.builder();
        final Elements timeElements = doc.select(TIME_DIVS_SELECTOR);

        timeElements
            .stream()
            .forEach(timeElement ->
                    times.add(LocalTime.parse(timeElement.text().toUpperCase(), timeFormat))
            );

        return times.build();
    }

    /**
     * Gets the image URL from the web page.
     * @param doc The web page the image is on.
     * @return The full URL of the image.
     */
    static String getImageUrl(final Document doc) {
        return BASE_IMAGE_URL + doc.select(WEBCAM_IMAGE_SELECTOR).attr("src");
    }
}
