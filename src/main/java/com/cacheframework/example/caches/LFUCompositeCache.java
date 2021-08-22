package com.cacheframework.example.caches;

import com.cacheframework.AbstractCache;
import com.cacheframework.exception.CacheConfigException;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class LFUCompositeCache extends AbstractCache<String, String>
{
    private static LFUCompositeCache instance;

    private LFUCompositeCache(  ) throws CacheConfigException
    {
        super( "lfuCompositeCache" );
    }


    public static LFUCompositeCache getInstance() throws CacheConfigException
    {
        if( instance == null )
        {
            instance = new LFUCompositeCache();
        }
        return instance;
    }
}
