package com.cacheframework.example.caches;


import com.cacheframework.exception.CacheConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LRUMemCacheTest
{

    private LRUMemCache cache;

    @BeforeEach
    void setUp() throws CacheConfigException
    {
        cache = LRUMemCache.getInstance();
    }

    @AfterEach
    void tearDown()
    {
        if( cache != null )
        {
            cache.clear();
        }
    }

    @Test
    void cacheTest()
    {
        //cache max size is set to 5 in cacheConfig yaml
        cache.put( "key1", "value1" );
        cache.put( "key2", "value2" );
        cache.put( "key3", "value3" );
        cache.put( "key4", "value4" );
        cache.put( "key5", "value5" );

        assertEquals( "value1", cache.get( "key1" ) );
        /*
         * since the cache size is at it's max size, adding a new cache entry will evict
         * cache entry which least recently used.
         * key1 is used recently and least recently used cache key is key2. So key2 will be removed.
         * least recently used - key2 -> key3 -> key4 -> key5 -> key1
         * */
        cache.put( "key6", "value6" ); //least recently used - key3 -> key4 -> key5 -> key1 -> key6 ->
        assertEquals( null, cache.get( "key2" ) );
        assertEquals( "value1", cache.get( "key1" ) ); //least recently used - key3 -> key4 -> key5 -> key6 -> key1 ->
        assertEquals( "value3", cache.get( "key3" ) ); //least recently used - key4 -> key5 -> key6 -> key1 -> key3 ->
        assertEquals( "value4", cache.get( "key4" ) ); //least recently used - key5 -> key6 -> key1 -> key3 -> key4 ->
        assertEquals( "value5", cache.get( "key5" ) ); //least recently used - key6 -> key1 -> key3 -> key4 -> key5 ->
        assertEquals( "value6", cache.get( "key6" ) ); //least recently used - key1 -> key3 -> key4 -> key5 -> key6 ->

        assertEquals( 5, cache.cacheSize() );

        /*
         * least recently used -  key1 -> key3 -> key4 -> key5 -> key6 ->
         * adding a new cache entry will remove key1
         * */

        cache.put( "key7", "value7" ); //least recently used - key3 -> key4 -> key5 -> key6 -> key7 ->
        assertEquals( null, cache.get( "key1" ) );
        assertEquals( 5, cache.cacheSize() );
    }
}