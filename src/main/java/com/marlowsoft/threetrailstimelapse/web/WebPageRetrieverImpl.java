package com.marlowsoft.threetrailstimelapse.web;

import com.google.common.base.Splitter;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.cache.JedisPoolRetriever;
import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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

    private static final Splitter urlSplitter = Splitter.on('?');
    private static final Splitter paramSplitter = Splitter.on('&');
    private static final Splitter argSplitter = Splitter.on('=');
    private static final Splitter spaceSplitter = Splitter.on(' ');

    @Override
    public Document getWebPage(String url) throws IOException {
        final RedisSettings redisSettings = Settings.getRedisSettings();
        final Document doc;

        if (redisSettings.getUseRedis()) {
            final JedisPoolRetriever jedisPoolRetriever = InjectorRetriever.getInjector().getInstance(JedisPoolRetriever.class);
            try (final Jedis jedis = jedisPoolRetriever.getJedisPool().getResource()) {
                // can we get this from the redis cache?
                if (jedis.exists(url)) {
                    // get the document from redis
                    doc = Jsoup.parse(jedis.get(url));
                } else {
                    // get the page from the internet and store it to redis
                    doc = Jsoup.connect(url).timeout(TIMEOUT).get();
                    if (!pageExists(doc)) {
                        throw new IOException("Page doesn't exist for url: " + url);
                    }
                    cleaner.clean(doc);
                    removeExtraneousElements(doc);
                    jedis.set(url, doc.toString());
                }
            }
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

    /**
     * Determine whether or not the page actually exists, or if the site simply returned the closest date
     * that it could. This is needed because a date going past the lower or upper bound of valid dates, e.g. one looking for
     * stuff back in 2014 (<a href="http://p-tn.net/pCAM/CERNERNE/archivepics.asp?m=7&d=30&y=2014">like this one</a>)
     * will return the page for July 30, 2015 which is the lower bound for pictures. Similar behavior happens at the
     * upper bound.
     * @param doc The page to check.
     * @return <code>true</code> if the page actually exists; <code>false</code> if the site simply delivered the
     * closest date it could.
     */
    private boolean pageExists(final Document doc) {
        final List<String> url = urlSplitter.splitToList(doc.baseUri());

        // if there are no arguments, the site will go to today, which is okay? what about 5 years from now?
        if (url.isEmpty()) {
            return true;
        }

        // get the calendar elements on the page
        final Element calElem = doc.select("td.bodytext.v-middle").get(0);
        final Elements dayElems = doc.select("table.calendar.calendarTable").select(".calendarselectedday");

        // something's definitely wrong if this isn't available
        if (dayElems.size() != 1) {
            return false;
        }

        // get the day, month, and year parameters
        String paramDay = "";
        String paramMonth = "";
        String paramYear = "";
        for (final String param : paramSplitter.split(url.get(1))) {
            final List<String> curArg = argSplitter.splitToList(param);
            if (curArg.size() == 2) {
                switch (curArg.get(0)) {
                    case "d":
                        paramDay = curArg.get(1);
                        break;
                    case "m":
                        paramMonth = Month.of(Integer.parseInt(curArg.get(1))).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                        break;
                    case "y":
                        paramYear = curArg.get(1);
                        break;
                    default:
                        break;
                }
            }
        }

        // not supplying all the parameters to the url will give undefined behavior
        if (paramDay.isEmpty() || paramMonth.isEmpty() || paramYear.isEmpty()) {
            return false;
        }

        // get the calendar day, month, and year
        final List<String> monthAndYear = spaceSplitter.splitToList(calElem.text());
        String calDay = dayElems.get(0).text();
        String calMonth = "";
        String calYear = "";
        if (monthAndYear.size() == 2) {
            calMonth = monthAndYear.get(0);
            calYear = monthAndYear.get(1);
        }

        // if the calendar elements equal the url parameters, then the page actually exists
        //  otherwise, the page doesn't actually exist and the site was simply doing some gratuitous rounding
        return paramDay.compareTo(calDay) == 0 && paramMonth.compareTo(calMonth) == 0 && paramYear.compareTo(calYear) == 0;
    }
}
