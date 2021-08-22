package com.cacheframework;

import com.cacheframework.cache.ICache;
import com.cacheframework.exception.CacheConfigException;

public abstract class AbstractCache<K, V>
{
    protected ICache cache;

    protected AbstractCache( String cacheName ) throws CacheConfigException
    {
        cache = CacheManager.getInstance().getCache( cacheName );
    }

    public V get( K key )
    {
        return ( V ) cache.getFromCache( key );
    }

    public void put( K key, V value )
    {
        cache.addToCache( key, value );
    }

    public void remove( K key )
    {
        cache.delete( key );
    }

    public void clear()
    {
        cache.clearCache();
    }

    public int cacheSize()
    {
        return cache.getSize();
    }
}
