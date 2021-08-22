package com.cacheframework.example.caches;

import com.cacheframework.AbstractCache;
import com.cacheframework.exception.CacheConfigException;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class LFUMemCache extends AbstractCache<String, String>
{
    private static LFUMemCache instance;

    private LFUMemCache( ) throws CacheConfigException
    {
        super( "lfuMemCache" );
    }

    public static LFUMemCache getInstance() throws CacheConfigException
    {
        if( instance == null )
        {
            instance = new LFUMemCache();
        }
        return instance;
    }
}
