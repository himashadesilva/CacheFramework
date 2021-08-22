package com.cacheframework.cache.memory.lru;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
class LRUMemoryCacheTest
{

    private LRUMemoryCache<String,String> cache;
    @BeforeEach
    void setUp()
    {
        cache = new LRUMemoryCache<>( "LRUCache", 5 );
    }

    @AfterEach
    void tearDown()
    {
        if( cache != null )
        {
            cache.clearCache();
        }
    }

    @Test
    void cacheTest()
    {
        cache.addToCache( "key1", "value1" );
        cache.addToCache( "key2", "value2" );
        cache.addToCache( "key3", "value3" );
        cache.addToCache( "key4", "value4" );
        cache.addToCache( "key5", "value5" );

        assertEquals( "value1", cache.getFromCache( "key1" ) );
        /*
         * since the cache size is at it's max size, adding a new cache entry will evict
         * cache entry which least recently used.
         * key1 is used recently and least recently used cache key is key2. So key2 will be removed.
         * least recently used - key2 -> key3 -> key4 -> key5 -> key1
         * */
        cache.addToCache( "key6", "value6" ); //least recently used - key3 -> key4 -> key5 -> key1 -> key6 ->
        assertEquals( null, cache.getFromCache( "key2" ) );
        assertEquals( "value1", cache.getFromCache( "key1" ) ); //least recently used - key3 -> key4 -> key5 -> key6 -> key1 ->
        assertEquals( "value3", cache.getFromCache( "key3" ) ); //least recently used - key4 -> key5 -> key6 -> key1 -> key3 ->
        assertEquals( "value4", cache.getFromCache( "key4" ) ); //least recently used - key5 -> key6 -> key1 -> key3 -> key4 ->
        assertEquals( "value5", cache.getFromCache( "key5" ) ); //least recently used - key6 -> key1 -> key3 -> key4 -> key5 ->
        assertEquals( "value6", cache.getFromCache( "key6" ) ); //least recently used - key1 -> key3 -> key4 -> key5 -> key6 ->

        assertEquals( 5, cache.getSize() );

        /*
         * least recently used -  key1 -> key3 -> key4 -> key5 -> key6 ->
         * adding a new cache entry will remove key1
         * */

        cache.addToCache( "key7", "value7" ); //least recently used - key3 -> key4 -> key5 -> key6 -> key7 ->
        assertEquals( null, cache.getFromCache( "key1" ) );
        assertEquals( 5, cache.getSize() );
    }
}