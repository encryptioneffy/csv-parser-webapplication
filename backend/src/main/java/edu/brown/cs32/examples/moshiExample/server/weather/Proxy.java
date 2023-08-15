package edu.brown.cs32.examples.moshiExample.server.weather;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import edu.brown.cs32.examples.moshiExample.server.Responder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Intermediary class between handler and utility
 */
public class Proxy {
    private final LoadingCache<Coordinates, Responder.WeatherResponse> cache;

    /**
     * Specifies cache size and how long data is kept in the cache
     * @param utility
     * @param proximityRadius
     * @param cacheMaxSize
     * @param expireAfter
     * @param expireAfterUnit
     */
    public Proxy(WeatherUtility utility, double proximityRadius, int cacheMaxSize, int expireAfter, TimeUnit expireAfterUnit) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(2, TimeUnit.HOURS)
                .recordStats()
                .build(
                        new CacheLoader<Coordinates, Responder.WeatherResponse>() {
                            @Override
                            public Responder.WeatherResponse load(Coordinates coords) throws IOException {
                                // If this isn't yet present in the cache, load it:
                                return utility.forecastGetter(coords);
                            }
                        });
    }

    /**
     * returns closest coordinate or key if distance is less than the specified radius else the coordinates inputted are
     * returned
     * @param coords
     * @return
     */
    public Coordinates getClosest(Coordinates coords) {
        Map<Coordinates, Responder.WeatherResponse> cacheMap = this.cache.asMap();
        for (Coordinates key : cacheMap.keySet()) {
            if (coords.getDistance(key) <= 0.3) {
                return key;
            }
        }
        return coords;
    }

    /**
     * forcast returned for a certain coordinate
     * @param coords
     * @return
     * @throws UncheckedExecutionException
     */
    public Responder.WeatherResponse getForecast(Coordinates coords) throws UncheckedExecutionException {
        Coordinates cached = this.getClosest(coords);
        Responder.WeatherResponse result = this.cache.getUnchecked(cached);
        System.out.println(this.cache.stats());
        return result;
    }

    /**
     * Only used for testing purposes, just a method to see if key exists in cache
     * @param coord
     * @return
     */
    public boolean isCachedExact(Coordinates coord) {
        return this.cache.asMap().containsKey(coord);
    }

}
