package com.marlowsoft.threetrailstimelapse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;

import com.marlowsoft.threetrailstimelapse.cache.ImageCache;
import com.marlowsoft.threetrailstimelapse.cache.WebPageCache;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
     * How long to wait, in seconds per image, to retrieve images from the website before giving up.
     */
    private static final int IMAGE_WAIT_TIME = 15;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Get all images for the specified day.
     * @param day The day to get images.
     * @return An immutable list of images from the specified day.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     */
    public List<BufferedImage> getDay(final LocalDate day) throws IOException, InterruptedException {
        final WebPageRetriever webPageRetriever = InjectorRetriever.getInjector().getInstance(WebPageRetriever.class);
        final List<LocalTime> times = WebPageParser.getTimes(webPageRetriever.getWebPage(URL_BASE + getParamString(day)));
        final List<String> timeUrls = Lists.newArrayList();

        times
            .stream()
            .forEach(time -> timeUrls.add(URL_BASE + getParamString(LocalDateTime.of(day, time))));

        return getImages(timeUrls);
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
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    public List<BufferedImage> getDateRange(final LocalDate beginDate, final LocalDate endDate,
                                            final LocalTime timeOfDay) throws IOException, InterruptedException, ExecutionException {
        final WebPageCache webPageCache = InjectorRetriever.getInjector().getInstance(WebPageCache.class);
        final List<String> pageUrls = Lists.newArrayList();
        LocalDate curDate = beginDate;

        while (curDate.compareTo(endDate) <= 0) {
            final String url = URL_BASE + getParamString(LocalDateTime.of(curDate, timeOfDay));
            if (WebPageParser.getTimes(webPageCache.getWebPage(url)).contains(timeOfDay)) {
                pageUrls.add(url);
            }
            curDate = curDate.plusDays(1);
        }

        return getImages(pageUrls);
    }

    /**
     * Gets all images between the specified dates at the specified time of day every day. If the exact time doesn't exist,
     * the <code>fuzziness</code> parameter will be used to search for a time <i>near</i> the time.
     * @param beginDate The beginning date.
     * @param endDate The end date.
     * @param timeOfDay The time of day to retrieve each day's image.
     * @param fuzziness The amount of time to add to the upper and lower bounds to search for a time
     *                  if the exact time doesn't exist on the page.
     * @return An immutable list of all images for the date range.
     * @throws IOException If something bad happens when retrieving the web page or images.
     * @throws InterruptedException If threading is interrupted unexpectedly.
     * @throws ExecutionException If cache retrieval fails.
     */
    public List<BufferedImage> getDateRange(final LocalDate beginDate, final LocalDate endDate,
                                            final LocalTime timeOfDay, final Duration fuzziness) throws
            IOException, InterruptedException, ExecutionException {
        final WebPageCache webPageCache = InjectorRetriever.getInjector().getInstance(WebPageCache.class);
        final List<String> pageUrls = Lists.newArrayList();
        LocalDate curDate = beginDate;

        while (curDate.compareTo(endDate) <= 0) {
            final String url = URL_BASE + getParamString(LocalDateTime.of(curDate, timeOfDay));
            final List<LocalTime> times = WebPageParser.getTimes(webPageCache.getWebPage(url));

            if (times.contains(timeOfDay)) {
                pageUrls.add(url);
            } else {
                // start looking for a time around the specified time
                final LocalTime lowerTime = timeOfDay.minus(fuzziness);
                final LocalTime upperTime = timeOfDay.plus(fuzziness);
                final List<LocalTime> fuzzyTimes = times.stream()
                        .filter(time -> time.compareTo(lowerTime) >= 0 && time.compareTo(upperTime) <= 0)
                        .collect(Collectors.toList());

                // find the time closest to the specified time
                if (fuzzyTimes.size() > 0) {
                    LocalTime closestTime = fuzzyTimes.get(0);
                    for (final LocalTime time : fuzzyTimes) {
                        if (Math.abs(time.get(ChronoField.MINUTE_OF_DAY) - timeOfDay.get(ChronoField.MINUTE_OF_DAY))
                            < Math.abs(closestTime.get(ChronoField.MINUTE_OF_DAY) - timeOfDay.get(ChronoField.MINUTE_OF_DAY))) {
                            closestTime = time;
                        }
                    }
                    pageUrls.add(URL_BASE + getParamString(LocalDateTime.of(curDate, closestTime)));
                }
            }
            curDate = curDate.plusDays(1);
        }

        return getImages(pageUrls);
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
     * Get all images from the specified urls that have a time lapse picture on them.
     * @param timeUrls Urls that have time lapse images to grab. e.g.
     *                 <code>http://p-tn.net/pCAM/CERNERNE/archivepics.asp?m=4&d=7&y=2016&h=06&min=59</code>
     * @return All images from the specified urls
     * @throws InterruptedException This function is multi-threaded. This will be thrown if something goes
     * wrong with the threads while retrieving images.
     */
    private List<BufferedImage> getImages(final List<String> timeUrls) throws InterruptedException {
        final WebPageCache webPageCache = InjectorRetriever.getInjector().getInstance(WebPageCache.class);
        final ImageCache imageCache = InjectorRetriever.getInjector().getInstance(ImageCache.class);
        final ImmutableList.Builder<BufferedImage> images = ImmutableList.builder();
        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        final Map<Integer, BufferedImage> imageMap = Collections.synchronizedMap(new HashMap<>());

        for (int timeUrlIdx = 0; timeUrlIdx < timeUrls.size(); timeUrlIdx++) {
            final int timeUrlIdxCopy = timeUrlIdx;
            executorService.execute(
                () -> {
                    try {
                        imageMap.put(timeUrlIdxCopy, imageCache.getImage(
                                WebPageParser.getImageUrl(webPageCache.getWebPage(timeUrls.get(timeUrlIdxCopy))))
                        );
                    } catch (final ExecutionException e) {
                        final StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));

                        logger.error("While attempting to get an image from...\r\n"
                            + timeUrls.get(timeUrlIdxCopy)
                            + "\r\n...this happened:\r\n"
                            + sw.toString() + "\r\n");
                    }
                }
            );
        }

        // wait for the threads to complete
        executorService.shutdown();
        executorService.awaitTermination(IMAGE_WAIT_TIME * timeUrls.size(), TimeUnit.SECONDS);

        // re-assemble the images back into order
        for (int imgIdx : imageMap.keySet()) {
            images.add(imageMap.get(imgIdx));
        }

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
        paramBuilder.append(dateTime.getHour());
        paramBuilder.append("&min=");
        paramBuilder.append(dateTime.get(ChronoField.MINUTE_OF_HOUR));

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
        paramBuilder.append(date.getMonthValue());
        paramBuilder.append("&d=");
        paramBuilder.append(date.getDayOfMonth());
        paramBuilder.append("&y=");
        paramBuilder.append(date.getYear());

        return paramBuilder.toString();
    }
}
