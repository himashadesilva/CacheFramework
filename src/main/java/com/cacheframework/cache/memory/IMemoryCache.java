package com.cacheframework.cache.memory;

import com.cacheframework.cache.ICache;

import java.util.List;

public interface IMemoryCache<K, V> extends ICache<K, V>
{
    List<K> getEvictKeys();
}
