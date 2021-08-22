package com.cacheframework.cache.memory;

import com.cacheframework.cache.ICache;

import java.util.List;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
public interface IMemoryCache<K, V> extends ICache<K, V>
{
    List<K> getEvictKeys();
}
