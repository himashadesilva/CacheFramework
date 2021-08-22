package com.cacheframework.example.caches;

import com.cacheframework.AbstractCache;
import com.cacheframework.exception.CacheConfigException;

public class LFUCompositeCache extends AbstractCache<String, String>
{
    public static LFUCompositeCache instance;

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
