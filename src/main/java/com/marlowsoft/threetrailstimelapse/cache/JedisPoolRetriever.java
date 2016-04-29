package com.marlowsoft.threetrailstimelapse.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * A thread-safe way to get a Jedis pool.
 */
@Singleton
public final class JedisPoolRetriever {
    private JedisPool jedisPool;

    @Inject
    private JedisPoolRetriever() {
        final RedisSettings settings = Settings.getRedisSettings();
        jedisPool = new JedisPool(
            new JedisPoolConfig(),
            "localhost",
            settings.getRedisPort(),
            Protocol.DEFAULT_TIMEOUT,
            settings.getRedisPassword()
        );
    }

    /**
     * Gets the Jedis pool.
     * @return The Jedis pool.
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
