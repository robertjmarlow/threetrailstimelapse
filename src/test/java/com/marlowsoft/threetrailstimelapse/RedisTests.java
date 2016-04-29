package com.marlowsoft.threetrailstimelapse;

import static com.marlowsoft.threetrailstimelapse.TestConstants.REDIS_PORT;
import static com.marlowsoft.threetrailstimelapse.TestConstants.REDIS_PWORD;
import static com.marlowsoft.threetrailstimelapse.TestConstants.SERVER_ADDR;
import static com.marlowsoft.threetrailstimelapse.TestConstants.SERVER_PORT;
import static org.junit.Assert.assertEquals;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.marlowsoft.threetrailstimelapse.bind.ConcreteModule;
import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.cache.JedisPoolRetriever;
import com.marlowsoft.threetrailstimelapse.mock.FakeWebServer;
import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

import org.jsoup.nodes.Document;
import org.junit.*;
import redis.clients.jedis.Jedis;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Tests a bunch of redis related stuff.
 * Feel free to un-@Ignore this suite if redis is up and running on this machine.
 */
@Ignore
public class RedisTests {
    private static final String CACHED_PAGE_NAME = SERVER_ADDR + "webpage.html";
    private static final String NOT_CACHED_PAGE_NAME = SERVER_ADDR + "webpage-midnight-times.html";
    private static final String CACHED_IMAGE_NAME = SERVER_ADDR + "regular_expressions.png";
    private static final String NOT_CACHED_IMAGE_NAME = SERVER_ADDR + "duty_calls.png";
    private static String webpageHTML;
    private static BufferedImage image;
    private static byte[] imageBytes;
    private static FakeWebServer fakeWebServer;
    private static Jedis jedis;
    private static WebPageRetriever webPageRetriever;
    private static ImageRetriever imageRetriever;

    @BeforeClass
    public static void suiteSetUp() throws IOException {
        Settings.setRedisSettings(new RedisSettings(true, REDIS_PORT, REDIS_PWORD));
        jedis = InjectorRetriever.getInjector().getInstance(JedisPoolRetriever.class).getJedisPool().getResource();
        Joiner joiner = Joiner.on("");
        webpageHTML = joiner.join(Files.asCharSource(new File("src/test/resources/webpage.html"), Charsets.UTF_8).readLines());
        Injector injector = Guice.createInjector(new ConcreteModule());
        webPageRetriever = injector.getInstance(WebPageRetriever.class);
        imageRetriever = injector.getInstance(ImageRetriever.class);
        fakeWebServer = new FakeWebServer(SERVER_PORT);

        image = ImageIO.read(new File("src/test/resources/regular_expressions.png"));
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        outputStream.flush();
        imageBytes = outputStream.toByteArray();
        outputStream.close();
    }

    @Before
    public void setUp() throws IOException {
        jedis.del(CACHED_PAGE_NAME);
        jedis.del(NOT_CACHED_PAGE_NAME);
        jedis.del(CACHED_IMAGE_NAME);
        jedis.del(NOT_CACHED_IMAGE_NAME);
    }

    @After
    public void tearDown() throws InterruptedException {
        jedis.del(CACHED_PAGE_NAME);
        jedis.del(NOT_CACHED_PAGE_NAME);
        jedis.del(CACHED_IMAGE_NAME);
        jedis.del(NOT_CACHED_IMAGE_NAME);

        // this delay between tests avoids a java.net.SocketException that occurs
        //  when a connection is closed while writing to it.
        //  not exactly sure why this is necessary, but
        //  http://stackoverflow.com/a/14624314
        Thread.sleep(750);
    }

    @AfterClass
    public static void suiteTearDown() {
        fakeWebServer.stop();
    }

    /**
     * Verifies that a web page is retrieved from the redis cache.
     * @throws IOException If a problem happens when getting the web page.
     */
    @Test
    public void getWebPageFromCache() throws IOException {
        jedis.set(CACHED_PAGE_NAME, webpageHTML);

        final Document doc = webPageRetriever.getWebPage(CACHED_PAGE_NAME);

        assertEquals(30, doc.select(".notpicked").size());
    }

    /**
     * Verifies that a web page is retrieved from the fake server.
     * @throws IOException If a problem happens when getting the web page.
     */
    @Test
    public void getWebPageNotFromCache() throws IOException {
        jedis.set(CACHED_PAGE_NAME, webpageHTML);

        final Document doc = webPageRetriever.getWebPage(NOT_CACHED_PAGE_NAME);

        assertEquals(47, doc.select(".notpicked").size());
    }

    /**
     * Verifies that an is retrieved from the redis cache.
     * @throws IOException If a problem happens when getting the image.
     */
    @Test
    public void getImageFromCache() throws IOException {
        jedis.set(CACHED_IMAGE_NAME.getBytes(), imageBytes);

        final BufferedImage bufferedImage = imageRetriever.getImage(CACHED_IMAGE_NAME);

        assertEquals(600, bufferedImage.getWidth());
        assertEquals(607, bufferedImage.getHeight());
    }

    /**
     * Verifies that an is retrieved from the fake server.
     * @throws IOException If a problem happens when getting the image.
     */
    @Test
    public void getImageNotFromCache() throws IOException {
        jedis.set(CACHED_IMAGE_NAME.getBytes(), imageBytes);

        final BufferedImage bufferedImage = imageRetriever.getImage(NOT_CACHED_IMAGE_NAME);

        assertEquals(300, bufferedImage.getWidth());
        assertEquals(330, bufferedImage.getHeight());
    }
}
