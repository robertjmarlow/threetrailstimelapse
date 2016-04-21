package com.marlowsoft.threetrailstimelapse;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Tests all classes associated with settings.
 */
public class SettingsTest {
    /**
     * Get redis settings from a file.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    @Test
    public void getRedisSettings() throws FileNotFoundException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        RedisSettings settings = gson.fromJson(new BufferedReader(new FileReader("src/test/resources/settings.json")), RedisSettings.class);

        assertEquals(true, settings.getUseRedis());
        assertEquals(6379, settings.getRedisPort());
        assertEquals("foobared", settings.getRedisPassword());
    }

    /**
     * Get redis settings from a file with an empty JSON string.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    @Test
    public void getEmptyRedisSettings() throws FileNotFoundException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        RedisSettings settings = gson.fromJson(new BufferedReader(
            new FileReader("src/test/resources/emptySettings.json")), RedisSettings.class);

        assertEquals(true, settings.getUseRedis());
        assertEquals(6379, settings.getRedisPort());
        assertEquals("foobared", settings.getRedisPassword());
    }

    /**
     * Gets redis settings after using the overloaded constructor.
     */
    @Test
    public void createRedisSettings() {
        final boolean useRedis = true;
        final int redisPort = 42;
        final String redisPassword = "hunter2";

        RedisSettings redisSettings = new RedisSettings(useRedis, redisPort, redisPassword);

        assertEquals(useRedis, redisSettings.getUseRedis());
        assertEquals(redisPort, redisSettings.getRedisPort());
        assertEquals(redisPassword, redisSettings.getRedisPassword());
    }

    /**
     * Verifies that redis settings can be get/set in {@link Settings}.
     */
    @Test
    public void setRedisSettings() {
        final boolean useRedis = true;
        final int redisPort = 42;
        final String redisPassword = "hunter2";

        RedisSettings redisSettings = new RedisSettings(useRedis, redisPort, redisPassword);

        Settings.setRedisSettings(redisSettings);

        assertEquals(useRedis, Settings.getRedisSettings().getUseRedis());
        assertEquals(redisPort, Settings.getRedisSettings().getRedisPort());
        assertEquals(redisPassword, Settings.getRedisSettings().getRedisPassword());
    }

    /**
     * Verifies that redis settings can be get/set via a file.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    @Test
    public void setRedisSettingsFromFile() throws FileNotFoundException {
        Settings.setRedisSettings("src/test/resources/settings.json");

        assertEquals(true, Settings.getRedisSettings().getUseRedis());
        assertEquals(6379, Settings.getRedisSettings().getRedisPort());
        assertEquals("foobared", Settings.getRedisSettings().getRedisPassword());
    }
}
