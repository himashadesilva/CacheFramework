package com.cacheframework.cache.composite;

import com.cacheframework.cache.ICache;
import com.cacheframework.cache.disk.DiskCache;
import com.cacheframework.cache.disk.IDiskCache;
import com.cacheframework.cache.memory.IMemoryCache;
import com.cacheframework.cache.memory.lfu.LFUMemoryCache;
import com.cacheframework.cache.memory.lru.LRUMemoryCache;
import com.cacheframework.config.Config;
import com.cacheframework.exception.CacheConfigException;

public class CompositeCache<K,V> implements ICache<K,V>
{
    private IMemoryCache<K,V> memoryCache;
    private IDiskCache<K,V> diskCache;
    private String cacheName;
    private Config config;
    private int memCacheSize;

    public CompositeCache( Config config ) throws CacheConfigException
    {
        this.config = config;
        this.cacheName = config.getCacheName();
        this.memCacheSize = config.getLevelOneCacheSize();
        init();
    }

    private void init() throws CacheConfigException
    {
        if( "LRU".equals( config.getEvictionStrategy() ) )
        {
            memoryCache = new LRUMemoryCache<>( cacheName, memCacheSize );
        }
        else if( "LFU".equals( config.getEvictionStrategy() ) )
        {
            memoryCache = new LFUMemoryCache<>( config.getCacheName(), config.getLevelTwoCacheSize() );
        }
        else
        {
            throw new CacheConfigException("Cache Config not setup properly for :"+ cacheName);
        }
        diskCache = new DiskCache<>( config );
    }

    @Override
    public V getFromCache( K key )
    {
        V value = memoryCache.getFromCache( key );
        if( value == null )
        {
            value = diskCache.getFromCache( key );
            if( value != null )
            {
                diskCache.delete( key );
                addToCache( key, value );
            }
        }
        return value;
    }

    @Override
    public void addToCache( K key, V value )
    {
        if( memoryCache.getSize() == memCacheSize )
        {
            memoryCache.getEvictKeys().forEach( evictKey ->
            {
                V evictValue = memoryCache.getFromCache( evictKey );
                diskCache.addToCache( evictKey, evictValue );
                memoryCache.delete( evictKey );
            });
        }
        memoryCache.addToCache( key, value );
    }

    @Override
    public void delete( K key )
    {
        memoryCache.delete( key );
        diskCache.delete( key );
    }

    @Override
    public int getSize()
    {
        return memoryCache.getSize() + diskCache.getSize();
    }

    @Override
    public void clearCache()
    {
        memoryCache.clearCache();
        diskCache.clearCache();
    }
}
