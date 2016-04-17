package com.marlowsoft.threetrailstimelapse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Various methods to retrieve images from the construction progress of
 * <a href="http://www.kansascity.com/news/business/development/article3845781.html">Cerner's Three Trails campus</a>.
 */
public class CampusImageRetriever {
    /**
     * The base of the URL to get images.
     */
    private static final String URL_BASE = "http://p-tn.net/pCAM/CERNERNE/archivepics.asp?";

    /**
     * The maximum number of threads to concurrently get images from the website.
     */
    private static final int THREAD_COUNT = 10;

    /**
     * How long to wait, in seconds, to retrieve images from the website before giving up.
     */
    private static final int IMAGE_WAIT_TIME = 30;

    /**
     * Get all images for the specified day.
     * @param day The day to get images.
     * @return An immutable list of images from the specified day.
     * @throws IOException If something bad happens when retrieving the web page.
     */
    public List<BufferedImage> getDay(final LocalDate day) throws IOException, InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        final ImmutableList.Builder<BufferedImage> images = ImmutableList.builder();
        final Map<Integer, BufferedImage> imageMap = Collections.synchronizedMap(new HashMap<>());
        final WebPageRetriever webPageRetriever = InjectorRetriever.getInjector().getInstance(WebPageRetriever.class);
        final ImageRetriever imageRetriever = InjectorRetriever.getInjector().getInstance(ImageRetriever.class);
        final List<LocalTime> times = WebPageParser.getTimes(webPageRetriever.getWebPage(URL_BASE + getParamString(day)));
        final List<String> timeUrls = Lists.newArrayList();

        times
            .stream()
            .forEach(time -> timeUrls.add(URL_BASE + getParamString(day.toLocalDateTime(time))));

//        for (final String timeUrl : timeUrls) {
        // for now, just grab a few images because their site is super-slow
        for (int timeUrlIdx = 0; timeUrlIdx < 5 && timeUrlIdx < times.size(); timeUrlIdx++) {
            final int timeUrlIdxCopy = timeUrlIdx;
            executorService.execute(
                () -> {
                    try {
                        imageMap.put(timeUrlIdxCopy, imageRetriever.getImage(
                                WebPageParser.getImageUrl(webPageRetriever.getWebPage(timeUrls.get(timeUrlIdxCopy))))
                        );
                    } catch (IOException e) {
                        // TODO log4j
                        e.printStackTrace();
                    }
                }
            );
        }

        // wait for the threads to complete
        executorService.shutdown();
        executorService.awaitTermination(IMAGE_WAIT_TIME, TimeUnit.SECONDS);

        for (int timeUrlIdx = 0; timeUrlIdx < 5 && timeUrlIdx < times.size(); timeUrlIdx++) {
            images.add(imageMap.get(timeUrlIdx));
        }

        return images.build();
    }

    /**
     * Get an image at the specified date and time.
     * <p>
     *     Note that not all dates and times are available! <code>null</code> will be returned if
     *     an image for that date and time isn't available.
     * </p>
     * @param dateTime The date and time to get an image.
     * @return The image for the specified date and time. <code>null</code> if the image isn't available for
     * the date and time.
     */
    public BufferedImage getDateTime(final LocalDateTime dateTime) {
        return null;
    }

    /**
     * Gets all images between the specified dates at the specified time of day every day.
     * @param beginDate The beginning date.
     * @param endDate The end date.
     * @param timeOfDay The time of day to retrieve each day's image.
     * @return An immutable list of all images for the date range.
     */
    public List<BufferedImage> getDateRange(final LocalDate beginDate, final LocalDate endDate, final LocalTime timeOfDay) {
        final ImmutableList.Builder<BufferedImage> images = ImmutableList.builder();

        return images.build();
    }

    /**
     * Gets all images between the specified dates at the specified times of day every day.
     * @param beginDate The beginning date.
     * @param endDate The end date.
     * @param timesOfDay The times of day to retrieve each day's images.
     * @return An immutable list of all images for the date range.
     */
    public List<BufferedImage> getDateRange(final LocalDate beginDate, final LocalDate endDate, final List<LocalTime> timesOfDay) {
        final ImmutableList.Builder<BufferedImage> images = ImmutableList.builder();

        return images.build();
    }

    /**
     * Gets a date time parameter string for the website based on the specified date and time.
     * @param dateTime The date and time to build a parameter string for.
     * @return A date time parameter string for the website.
     */
    private String getParamString(final LocalDateTime dateTime) {
        final StringBuilder paramBuilder = new StringBuilder();

        paramBuilder.append(getParamString(dateTime.toLocalDate()));
        paramBuilder.append("&h=");
        paramBuilder.append(dateTime.getHourOfDay());
        paramBuilder.append("&min=");
        paramBuilder.append(dateTime.getMinuteOfHour());

        return paramBuilder.toString();
    }

    /**
     * Gets a date parameter string for the website based on the specified date.
     * @param date The date to build a parameter string for.
     * @return A date parameter string for the website.
     */
    private String getParamString(final LocalDate date) {
        final StringBuilder paramBuilder = new StringBuilder();

        paramBuilder.append("m=");
        paramBuilder.append(date.getMonthOfYear());
        paramBuilder.append("&d=");
        paramBuilder.append(date.getDayOfMonth());
        paramBuilder.append("&y=");
        paramBuilder.append(date.getYear());

        return paramBuilder.toString();
    }
}
