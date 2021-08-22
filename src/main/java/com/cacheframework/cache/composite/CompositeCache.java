package com.cacheframework.cache.composite;

import com.cacheframework.cache.ICache;
import com.cacheframework.cache.disk.DiskCache;
import com.cacheframework.cache.disk.IDiskCache;
import com.cacheframework.cache.memory.IMemoryCache;
import com.cacheframework.cache.memory.lfu.LFUMemoryCache;
import com.cacheframework.cache.memory.lru.LRUMemoryCache;
import com.cacheframework.config.Config;
import com.cacheframework.exception.CacheConfigException;

/**
 * Composite Cache Implementation with memory cache as level one cache and disk cache as level 2 cache.
 * Cache eviction strategy will be chose by EvictionStrategy in {@link Config}. Cache entries in memory cache
 * will be spooled into the disk if the memory cache is full. Cache entries to be spool are taken from eviction strategy.
 * if the disk cache is also reached to max cache size, disk cache will evict cache entries using eviction strategy.
 *
 * During a memory cache miss, it will check in disk cache and if there is a hit, cache value will be returned and it will
 * put into memory cache.
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
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
