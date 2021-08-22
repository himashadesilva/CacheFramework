package com.cacheframework.example.caches;

import com.cacheframework.AbstractCache;
import com.cacheframework.exception.CacheConfigException;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class LRUMemCache extends AbstractCache<String, String>
{
    private static LRUMemCache instance;

    private LRUMemCache() throws CacheConfigException
    {
        super("lruMemCache");
    }

    public static LRUMemCache getInstance() throws CacheConfigException
    {
        if( instance == null )
        {
            instance = new LRUMemCache();
        }
        return instance;
    }

}
