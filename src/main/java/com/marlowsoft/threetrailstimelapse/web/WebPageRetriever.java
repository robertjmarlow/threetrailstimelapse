package com.marlowsoft.threetrailstimelapse.web;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Defines methods to retrieve a web page from the internet.
 */
public interface WebPageRetriever {
    Document getWebPage(String url) throws IOException;
}
