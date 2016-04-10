package com.marlowsoft.threetrailstimelapse;

import com.google.common.collect.ImmutableList;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Various methods to retrieve images from the construction progress of
 * <a href="http://www.kansascity.com/news/business/development/article3845781.html">Cerner's Three Trails campus</a>.
 */
public class CampusImageRetriever {
    /**
     * Get all images for the specified day.
     * @param day The day to get images.
     * @return An immutable list of images from the specified day.
     */
    public List<BufferedImage> getDay(final LocalDate day) {
        final ImmutableList.Builder<BufferedImage> images = ImmutableList.builder();

        return images.build();
    }

    /**
     * Get an image at the specified date and time.
     * <p>
     * Note that not all dates and times are available! <code>null</code> will be returned if
     * an image for that date and time isn't available.
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
}
