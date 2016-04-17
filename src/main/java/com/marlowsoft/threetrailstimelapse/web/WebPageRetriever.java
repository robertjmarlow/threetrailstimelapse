package com.marlowsoft.threetrailstimelapse.web;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Defines methods to retrieve a web page from the internet.
 */
public interface WebPageRetriever {
    /**
     * Get the web page at the specified url.
     * @param url The url of the web page.
     * @return The web page.
     * @throws IOException If something goes wrong while getting the web page.
     */
    Document getWebPage(String url) throws IOException;
}
