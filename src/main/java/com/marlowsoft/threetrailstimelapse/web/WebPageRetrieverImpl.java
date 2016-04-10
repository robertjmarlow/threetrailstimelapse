package com.marlowsoft.threetrailstimelapse.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Concrete implementation of {@link WebPageRetriever}. Retrieves an actual web page.
 */
public class WebPageRetrieverImpl implements WebPageRetriever {
    final private static int TIMEOUT = 10000;

    @Override
    public Document getWebPage(String url) throws IOException {
        return Jsoup.connect(url).timeout(TIMEOUT).get();
    }
}
