package com.cacheframework.cache.memory.lfu;


import com.cacheframework.cache.CacheElement;
import com.cacheframework.cache.memory.IMemoryCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * LFU cache implementation. Cache key eviction will be done using LFU algorithm. Frequency LinkedHashSet
 * is stored as an array. This cache works as Hybrid with LRU. During the cache key eviction, it will get
 * cache keys with least frequency and from that LinkedHashSet it will get least recently used one to evict.
 * And also during cache hit, it will update frequency list array and LinkedHashSet in order to put most recently
 * accessed elements ahead of less recently ones. Cache values are stored as {@link CacheElement}.
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
public class LFUMemoryCache<K, V> implements IMemoryCache<K,V>
{

    private int maxSize;
    private String cacheName;
    private Map<K,CacheElement<K,V>> cache;
    private LinkedHashSet[] frequencyList;
    private int lowestFrequency;
    private int maxFrequency;


    public LFUMemoryCache( String cacheName, int maxSize )
    {
        this.maxSize = maxSize;
        this.cacheName = cacheName;
        this.cache = new HashMap<>( maxSize );
        this.frequencyList = new LinkedHashSet[maxSize];
        this.lowestFrequency = 0;
        this.maxFrequency = maxSize - 1;
        initFrequencyList();
    }

    @Override
    public V getFromCache( K key )
    {
        CacheElement<K,V> currentNode = cache.get( key );
        if( currentNode != null )
        {
            int currentFrequency = currentNode.getFrequency();
            if( currentFrequency < maxFrequency )
            {
                int nextFrequency = currentFrequency + 1;
                LinkedHashSet<CacheElement<K,V>> currentNodes = frequencyList[currentFrequency];
                LinkedHashSet<CacheElement<K,V>> newNodes = frequencyList[nextFrequency];
                moveToNextFrequency( currentNode, nextFrequency, currentNodes, newNodes );
                cache.put( key, currentNode );
                if( lowestFrequency == currentFrequency && currentNodes.isEmpty() )
                {
                    lowestFrequency = nextFrequency;
                }
            }
            else
            {
                // Hybrid with LRU: put most recently accessed ahead of others:
                LinkedHashSet<CacheElement<K,V>> nodes = frequencyList[currentFrequency];
                nodes.remove( currentNode );
                nodes.add( currentNode );
            }
            return currentNode.getValue();
        }
        else
        {
            return null;
        }
    }

    @Override
    public void addToCache( K key, V value )
    {
        CacheElement<K,V> currentNode = cache.get( key );
        if( currentNode == null )
        {
            if( cache.size() == maxSize )
            {
                doEviction();
            }
            LinkedHashSet<CacheElement<K,V>> nodes = frequencyList[0];
            currentNode = new CacheElement<>( key, value, cacheName );
            nodes.add( currentNode );
            cache.put( key, currentNode );
            lowestFrequency = 0;
        }
        else
        {
            currentNode.setValue( value );
        }
    }

    @Override
    public void delete( K key )
    {
        CacheElement<K,V> currentNode = cache.remove( key );
        if( currentNode != null )
        {
            LinkedHashSet<CacheElement<K,V>> nodes = frequencyList[currentNode.getFrequency()];
            nodes.remove( currentNode );
            if( lowestFrequency == currentNode.getFrequency() )
            {
                findNextLowestFrequency();
            }
        }
    }


    @Override
    public int getSize()
    {
        return cache.size();
    }

    @Override
    public void clearCache()
    {
        cache.clear();
        frequencyList = new LinkedHashSet[maxSize];
    }

    private void initFrequencyList()
    {
        for( int i = 0; i <= maxFrequency; i++ )
        {
            frequencyList[i] = new LinkedHashSet<CacheElement<K,V>>();
        }
    }

    private void doEviction()
    {
        LinkedHashSet<CacheElement<K,V>> nodes = frequencyList[lowestFrequency];
        if( nodes.isEmpty() )
        {
            throw new IllegalStateException( "Lowest frequency constraint violated!" );
        }
        else
        {
            Iterator<CacheElement<K,V>> it = nodes.iterator();
            if( it.hasNext() )
            {
                CacheElement<K,V> node = it.next();
                it.remove();
                cache.remove( node.getKey() );
            }
            else if( !it.hasNext() )
            {
                findNextLowestFrequency();
            }
        }
    }

    private void findNextLowestFrequency()
    {
        while( lowestFrequency <= maxFrequency && frequencyList[lowestFrequency].isEmpty() )
        {
            lowestFrequency++;
        }
        if( lowestFrequency > maxFrequency )
        {
            lowestFrequency = 0;
        }
    }

    private void moveToNextFrequency( CacheElement<K,V> currentNode, int nextFrequency, LinkedHashSet<CacheElement<K,V>> currentNodes, LinkedHashSet<CacheElement<K,V>> newNodes )
    {
        currentNodes.remove( currentNode );
        newNodes.add( currentNode );
        currentNode.setFrequency( nextFrequency );
    }

    @Override
    public List<K> getEvictKeys()
    {
        List<K> evictKeys = new ArrayList<>();
        LinkedHashSet<CacheElement<K,V>> nodes = frequencyList[lowestFrequency];
        Iterator<CacheElement<K,V>> it = nodes.iterator();
        if( it.hasNext() )
        {
            CacheElement<K,V> node = it.next();
            evictKeys.add( node.getKey() );
        }
        return evictKeys;
    }
}
