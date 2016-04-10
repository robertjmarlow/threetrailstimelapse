package com.marlowsoft.threetrailstimelapse.bind;

import com.google.inject.AbstractModule;
import com.marlowsoft.threetrailstimelapse.mock.FakeImageRetrieverImpl;
import com.marlowsoft.threetrailstimelapse.mock.FakeWebPageRetrieverImpl;
import com.marlowsoft.threetrailstimelapse.web.ImageRetriever;
import com.marlowsoft.threetrailstimelapse.web.WebPageRetriever;

/**
 * Provides fake implementations of interfaces.
 */
public class FakeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ImageRetriever.class).to(FakeImageRetrieverImpl.class);
        bind(WebPageRetriever.class).to(FakeWebPageRetrieverImpl.class);
    }
}
