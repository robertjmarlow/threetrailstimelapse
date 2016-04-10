package com.marlowsoft.threetrailstimelapse.bind;

import com.google.inject.AbstractModule;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.ImageRetrieverImpl;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetrieverImpl;

/**
 * Provides concrete implementations of interfaces.
 */
final public class ConcreteModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ImageRetriever.class).to(ImageRetrieverImpl.class);
        bind(WebPageRetriever.class).to(WebPageRetrieverImpl.class);
    }
}
