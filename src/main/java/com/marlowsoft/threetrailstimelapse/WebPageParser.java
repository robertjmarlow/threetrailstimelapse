package com.marlowsoft.threetrailstimelapse;

import com.google.common.collect.ImmutableList;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String BASE_IMAGE_URL = "http://p-tn.net/pCAM/CERNERNE/";

    private static final String TIME_DIVS_SELECTOR = "#pickTime div";
    private static final String WEBCAM_IMAGE_SELECTOR = ".image img";

    private static final Pattern MIDNIGHT_TIME = Pattern.compile("00:([0-9]{2})am");

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
            .forEach(timeElement -> {
                    // the site has times near midnight display as "00:xyam"
                    //  joda doesn't like this, so adjust the string to "12:xyam" if needed
                    String timeText = timeElement.text();
                    final Matcher matcher = MIDNIGHT_TIME.matcher(timeText);
                    if (matcher.matches()) {
                        timeText = "12:" + matcher.group(1) + "am";
                    }
                    times.add(timeFormat.parseLocalTime(timeText));
                }
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
