package com.cacheframework.cache.memory.lru;

import com.cacheframework.cache.CacheElement;
import com.cacheframework.cache.memory.IMemoryCache;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *  LRU cache implementation. Cache key eviction will be done using LRU algorithm. Cache values are stored
 *  in Hashmap and the cache keys are stored in Doubly-linked list. LRU strategy is maintain by Doubly-linked list
 *  and cache values are taken from the hashmap. Cache values are stored as {@link CacheElement}.
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
public class LRUMemoryCache<K, V> implements IMemoryCache<K, V>
{
    private int maxSize;
    private String cacheName;
    private Deque<K> deque;
    private Map<K, CacheElement<K,V>> cacheValues;

    public LRUMemoryCache( String cacheName, int maxSize )
    {
        this.cacheValues = new HashMap<>( maxSize );
        this.deque = new LinkedList<>();
        this.maxSize = maxSize;
        this.cacheName = cacheName;
    }


    @Override
    public V getFromCache( K key )
    {
        if( cacheValues.containsKey(key))
        {
            CacheElement<K,V> current = cacheValues.get(key);
            deque.remove(current.getKey());
            deque.addFirst( current.getKey() );
            return current.getValue();
        }
        return null;
    }

    @Override
    public void addToCache( K key, V value )
    {
        if( cacheValues.containsKey( key ) )
        {
            CacheElement<K,V> cacheElement = cacheValues.get(key);
            deque.remove(cacheElement.getKey());
        }
        else
        {
            if( deque.size() == maxSize )
            {
                K temp = deque.removeLast();
                cacheValues.remove(temp);
            }
        }
        CacheElement<K,V> newItem = new CacheElement<>(key, value,cacheName);
        deque.addFirst( newItem.getKey() );
        cacheValues.put(key, newItem);
    }

    @Override
    public void delete( K key )
    {
        if( cacheValues.containsKey( key ) )
        {
            CacheElement<K,V> cacheElement = cacheValues.remove(key);
            deque.remove(cacheElement.getKey());
        }
    }

    @Override
    public int getSize()
    {
        return cacheValues.size();
    }

    @Override
    public void clearCache()
    {
        cacheValues.clear();
        deque.clear();
    }

    @Override
    public List<K> getEvictKeys()
    {
        List<K> evictKeys = new ArrayList<>();
        evictKeys.add( deque.getLast() );
        return evictKeys;
    }
}
