package com.marlowsoft.threetrailstimelapse;

import static com.marlowsoft.threetrailstimelapse.TestConstants.SERVER_ADDR;
import static com.marlowsoft.threetrailstimelapse.TestConstants.SERVER_PORT;
import static org.junit.Assert.assertEquals;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.marlowsoft.threetrailstimelapse.bind.ConcreteModule;
import com.marlowsoft.threetrailstimelapse.mock.FakeWebServer;
import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Tests {@link com.marlowsoft.threetrailstimelapse.web.ImageRetrieverImpl}.
 */
public class ImageRetrieverTest {
    /**
     * Gets an image from a fake server running locally.
     * @throws IOException If the image doesn't exist on disk
     */
    @Test
    public void getImage() throws IOException {
        final Injector injector = Guice.createInjector(new ConcreteModule());
        final FakeWebServer fakeWebServer = new FakeWebServer(SERVER_PORT);
        final ImageRetriever imageRetriever = injector.getInstance(ImageRetriever.class);

        Settings.setRedisSettings(new RedisSettings(false, 0, ""));

        final BufferedImage image = imageRetriever.getImage(SERVER_ADDR + "regular_expressions.png");

        assertEquals(600, image.getWidth());
        assertEquals(607, image.getHeight());

        fakeWebServer.stop();
    }
}
