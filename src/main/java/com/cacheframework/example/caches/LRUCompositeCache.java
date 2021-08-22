package com.cacheframework.example.caches;

import com.cacheframework.AbstractCache;
import com.cacheframework.exception.CacheConfigException;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class LRUCompositeCache extends AbstractCache<String, String>
{
    private static LRUCompositeCache instance;

    private LRUCompositeCache(  ) throws CacheConfigException
    {
        super( "lruCompositeCache" );
    }


    public static LRUCompositeCache getInstance() throws CacheConfigException
    {
        if( instance == null )
        {
            instance = new LRUCompositeCache();
        }
        return instance;
    }
}
