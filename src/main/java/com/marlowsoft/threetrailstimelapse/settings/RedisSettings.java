package com.marlowsoft.threetrailstimelapse.settings;

/**
 * Stores settings related to redis. This class is immutable.
 */
public class RedisSettings {
    private static final int DEFAULT_PORT = 6379;
    private static final String DEFAULT_PASS = "foobared";

    private boolean useRedis;
    private int redisPort;
    private String redisPassword;

    /**
     * Default constructor. Will initialize...
     * <ul>
     *     <li><code>useRedis</code> to <code>true</code></li>
     *     <li><code>redisPort</code> to <code>6379</code></li>
     *     <li><code>redisPassword</code> to <code>"foobared"</code></li>
     * </ul>
     */
    public RedisSettings() {
        useRedis = true;
        redisPort = DEFAULT_PORT;
        redisPassword = DEFAULT_PASS;
    }

    /**
     * Overloaded constructor.
     * @param useRedis Whether or not to use redis.
     * @param redisPort The port the redis instance is running on.
     * @param redisPassword The password to connect to the redis instance with. If no password is being used,
     *                      set this to <code>null</code>.
     */
    public RedisSettings(boolean useRedis, int redisPort, String redisPassword) {
        this.useRedis = useRedis;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
    }

    /**
     * Gets whether or not to use redis.
     * @return Whether or not to use redis.
     */
    public boolean getUseRedis() {
        return useRedis;
    }

    /**
     * Gets the port the redis instance is running on.
     * @return The port the redis instance is running on.
     */
    public int getRedisPort() {
        return redisPort;
    }

    /**
     * Gets the password to connect to the redis instance with.
     * @return The password to connect to the redis instance with.
     */
    public String getRedisPassword() {
        return redisPassword;
    }
}
