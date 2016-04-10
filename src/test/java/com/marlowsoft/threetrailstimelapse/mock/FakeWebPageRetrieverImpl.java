package com.marlowsoft.threetrailstimelapse.mock;

import com.google.common.base.Charsets;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * Always returns the specified web page from the resources directory.
 */
public class FakeWebPageRetrieverImpl implements WebPageRetriever {
    @Override
    public Document getWebPage(String url) throws IOException {
        return Jsoup.parse(new File("src/test/resources/" + url), Charsets.UTF_8.name());
    }
}
