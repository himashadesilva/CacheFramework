package com.cacheframework.cache;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
public interface ICache<K, V>
{
    V getFromCache( K key );

    void addToCache( K key, V value);

    void delete(K key);

    int getSize();

    void clearCache();
}
