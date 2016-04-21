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
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests {@link com.marlowsoft.threetrailstimelapse.web.WebPageRetrieverImpl}.
 */
public class WebPageRetrieverTest {
    /**
     * Gets a webpage from a fake server running locally.
     * @throws IOException If the webpage doesn't exist on disk
     */
    @Test
    public void getWebPage() throws IOException {
        final Injector injector = Guice.createInjector(new ConcreteModule());
        final FakeWebServer fakeWebServer = new FakeWebServer(SERVER_PORT);
        final WebPageRetriever webPageRetriever = injector.getInstance(WebPageRetriever.class);

        Settings.setRedisSettings(new RedisSettings(false, 0, ""));

        final Document document = webPageRetriever.getWebPage(SERVER_ADDR + "webpage.html");

        assertEquals(7, document.select("table").size());

        fakeWebServer.stop();
    }
}
