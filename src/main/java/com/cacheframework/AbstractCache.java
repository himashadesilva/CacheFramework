package com.cacheframework;

import com.cacheframework.cache.ICache;
import com.cacheframework.exception.CacheConfigException;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
public abstract class AbstractCache<K, V>
{
    protected ICache<K,V> cache;

    protected AbstractCache( String cacheName ) throws CacheConfigException
    {
        cache = CacheManager.getInstance().getCache( cacheName );
    }

    public V get( K key )
    {
        return cache.getFromCache( key );
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
