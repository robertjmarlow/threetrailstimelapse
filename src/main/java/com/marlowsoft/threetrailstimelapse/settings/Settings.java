package com.marlowsoft.threetrailstimelapse.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * All settings for this library, and means to get them.
 */
public final class Settings {
    private static final Gson gson = new GsonBuilder().create();
    private static RedisSettings redisSettings = new RedisSettings();

    /**
     * Private constructor to prevent instantiation.
     */
    private Settings() {}

    /**
     * Get the settings for redis.
     * @return The settings for redis.
     */
    public static RedisSettings getRedisSettings() {
        return redisSettings;
    }

    /**
     * Set the settings for redis.
     * @param redisSettings The settings for redis.
     */
    public static synchronized void setRedisSettings(RedisSettings redisSettings) {
        Settings.redisSettings = redisSettings;
    }

    /**
     * Set the settings for redis via a file.
     * @param jsonSettingsFile The path of the file to set the redis settings.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    public static synchronized void setRedisSettings(String jsonSettingsFile) throws FileNotFoundException {
        Settings.redisSettings = gson.fromJson(new BufferedReader(new FileReader(jsonSettingsFile)), RedisSettings.class);
    }
}
