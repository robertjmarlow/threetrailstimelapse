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
    private static final String FAKE_PAGE_BASE_URI = "src/test/resources/";
    private static String fakePage = "webpage.html";

    /**
     * Sets the name of the webpage to return from the test resources directory.
     * @param fakePage The name of the html file in the resources directory to always return in this fake implementation.
     */
    public static synchronized void setFakePage(final String fakePage) {
        FakeWebPageRetrieverImpl.fakePage = fakePage;
    }

    @Override
    public Document getWebPage(String url) throws IOException {
        return Jsoup.parse(new File(FAKE_PAGE_BASE_URI + fakePage), Charsets.UTF_8.name());
    }
}
