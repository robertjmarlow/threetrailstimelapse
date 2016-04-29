package com.marlowsoft.threetrailstimelapse.web;

import com.marlowsoft.threetrailstimelapse.bind.InjectorRetriever;
import com.marlowsoft.threetrailstimelapse.cache.JedisPoolRetriever;
import com.marlowsoft.threetrailstimelapse.settings.RedisSettings;
import com.marlowsoft.threetrailstimelapse.settings.Settings;
import redis.clients.jedis.Jedis;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Concrete implementation of {@link ImageRetriever}. Retrieves an actual image from the web.
 */
public class ImageRetrieverImpl implements ImageRetriever{
    @Override
    public BufferedImage getImage(final String url) throws IOException {
        final BufferedImage image;
        final RedisSettings redisSettings = Settings.getRedisSettings();

        if (redisSettings.getUseRedis()) {
            final JedisPoolRetriever jedisPoolRetriever = InjectorRetriever.getInjector().getInstance(JedisPoolRetriever.class);
            final Jedis jedis = jedisPoolRetriever.getJedisPool().getResource();
            final byte[] urlBytes = url.getBytes();

            // can we get this from the redis cache?
            if (jedis.exists(urlBytes)) {
                // get the image from redis
                image = ImageIO.read(new ByteArrayInputStream(jedis.get(urlBytes)));
            } else {
                // get the image from the internet and store the binary image to redis
                image = ImageIO.read(new URL(url));
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                outputStream.flush();
                final byte[] bytes = outputStream.toByteArray();
                outputStream.close();

                jedis.set(urlBytes, bytes);
            }

            jedis.close();
        } else {
            image = ImageIO.read(new URL(url));
        }

        return image;
    }
}
