package com.cacheframework.cache;

import java.io.Serializable;

/**
 *  Cache element used to cache objects
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache element key Type
 * @param <V> cache element value Type
 */
public class CacheElement<K,V> implements Serializable
{
    private static final long serialVersionUID = -5182874521642030726L;
    private K key;
    private V value;
    private String cacheName;
    private int frequency;

    public CacheElement( K key, V value, String cacheName )
    {
        this.key = key;
        this.value = value;
        this.cacheName = cacheName;
    }

    public K getKey()
    {
        return key;
    }

    public void setKey( K key )
    {
        this.key = key;
    }

    public V getValue()
    {
        return value;
    }

    public void setValue( V value )
    {
        this.value = value;
    }

    public String getCacheName()
    {
        return cacheName;
    }

    public void setCacheName( String cacheName )
    {
        this.cacheName = cacheName;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void setFrequency( int frequency )
    {
        this.frequency = frequency;
    }
}
