package com.marlowsoft.threetrailstimelapse.web;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.cache.JedisPoolRetriever;
import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * Concrete implementation of {@link WebPageRetriever}. Retrieves an actual web page.
 */
public class WebPageRetrieverImpl implements WebPageRetriever {
    private static final int TIMEOUT = 30000;

    private static final String EXTRANEOUS_ELEMS = "#clientLogo, #weather, td.left, #rightLogo, #menu";

    private static final Cleaner cleaner = new Cleaner(
            Whitelist.relaxed()
                .addAttributes(":all", "id", "class")
                .preserveRelativeLinks(true)
    );

    @Override
    public Document getWebPage(String url) throws IOException {
        final RedisSettings redisSettings = Settings.getRedisSettings();
        final Document doc;

        if (redisSettings.getUseRedis()) {
            final JedisPoolRetriever jedisPoolRetriever = InjectorRetriever.getInjector().getInstance(JedisPoolRetriever.class);
            final Jedis jedis = jedisPoolRetriever.getJedisPool().getResource();

            // can we get this from the redis cache?
            if (jedis.exists(url)) {
                // get the document from redis
                doc = Jsoup.parse(jedis.get(url));
            } else {
                // get the page from the internet and store it to redis
                doc = cleaner.clean(Jsoup.connect(url).timeout(TIMEOUT).get());
                removeExtraneousElements(doc);
                jedis.set(url, doc.toString());
            }

            jedis.close();
        } else {
            doc = cleaner.clean(Jsoup.connect(url).timeout(TIMEOUT).get());
        }

        return doc;
    }

    /**
     * Get rid of all DOM elements that are irrelevant to getting images.
     * @param doc The document to clean.
     */
    private void removeExtraneousElements(final Document doc) {
        doc.select(EXTRANEOUS_ELEMS).remove();
        Elements elements = doc.select("table");
        elements.get(1).remove();
        elements.get(2).remove();
        elements.get(4).remove();
    }
}
