package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertTrue;

import com.marlowsoft.threetrailstimelapse.bind.FakeModule;
import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.encode.VideoEncoder;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ExecutionException;

/**
 * Tests {@link VideoEncoder}.
 */
public class VideoEncoderTest {
    private static final String TEST_VIDEO_FILE_NAME = "./src/test/resources/cool.webm";

    @Test
    public void testEncode() throws IOException, InterruptedException, ExecutionException {
        InjectorRetriever.setInjector(new FakeModule());
        final CampusImageRetriever retriever = new CampusImageRetriever();
        final LocalDate beginDate = LocalDate.of(2016, 1, 1);
        final LocalDate endDate = LocalDate.of(2016, 1, 15);
        final LocalTime timeOfDay = LocalTime.of(12, 0);

        VideoEncoder.encode(retriever.getDateRange(beginDate, endDate, timeOfDay), TEST_VIDEO_FILE_NAME);

        final Path testVideo = Paths.get(TEST_VIDEO_FILE_NAME);

        assertTrue(Files.exists(testVideo));

        Files.delete(testVideo);
    }
}
