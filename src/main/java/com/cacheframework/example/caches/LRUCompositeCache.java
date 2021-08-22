package com.cacheframework.example.caches;

import com.cacheframework.AbstractCache;
import com.cacheframework.exception.CacheConfigException;

public class LRUCompositeCache extends AbstractCache<String, String>
{
    public static LRUCompositeCache instance;

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
