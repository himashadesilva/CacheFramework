package com.cacheframework.cache;

public interface ICache<K, V>
{
    V getFromCache( K key );

    void addToCache( K key, V value);

    void delete(K key);

    int getSize();

    void clearCache();
}
