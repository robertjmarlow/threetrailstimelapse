package com.marlowsoft.threetrailstimelapse.bind;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Gets an {@link com.google.inject.Injector}. By default, will create a {@link ConcreteModule}.
 */
public final class InjectorRetriever {
    private static Injector injector = Guice.createInjector(new ConcreteModule());

    /**
     * Private constructor to prevent instantiation.
     */
    private InjectorRetriever() {}

    /**
     * Get an instance of the injector.
     * @return An injector.
     */
    public static Injector getInjector() {
        return injector;
    }

    /**
     * Sets the module that will create the injector.
     * <p>
     *     Note this is <i>not</i> thread-safe for anyone using {@link #getInjector()}! It is <i>not</i>
     *     recommended to use this when other threads are using {@link #getInjector()}!
     * </p>
     * @param module The module that will create the injector.
     */
    public static synchronized void setInjector(final AbstractModule module) {
        injector = Guice.createInjector(module);
    }
}
