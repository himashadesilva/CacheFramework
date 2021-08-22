package com.cacheframework.example.caches;

import com.cacheframework.exception.CacheConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LFUMemCacheTest
{

    private LFUMemCache cache;

    @BeforeEach
    void setUp() throws CacheConfigException
    {
        cache = LFUMemCache.getInstance();
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
        cache.put( "key5", "value5" ); //cache size is reach to it's max size

        assertEquals( "value1", cache.get( "key1" ) ); //key1 frequency is now 1.
        assertEquals( "value3", cache.get( "key3" ) ); //key3 frequency is now 1.


        /*
         * since the cache size is at it's max size, adding a new cache entry will evict
         * cache entry with lowest frequency and least recentlyy used
         * frequency 0 - key2, key4, key5
         * frequency 1 - key1, key3
         * key2 will be removed. and cache size will be 5(with new entry). (key4, key5,key6, key1, key3, )
         * */
        cache.put( "key6", "value6" );
        assertEquals( null, cache.get( "key2" ) );
        assertEquals( 5, cache.cacheSize() );
        /*
         * frequency 0 - key4, key5, key6
         * frequency 1 - key1, key3
         * */

        assertEquals( "value6", cache.get( "key6" ) );
        assertEquals( "value6", cache.get( "key6" ) );
        assertEquals( "value1", cache.get( "key1" ) );
        assertEquals( "value1", cache.get( "key1" ) );

        /*
         * frequency 0 - key4, key5,
         * frequency 1 - key3
         * frequency 2 - key6
         * frequency 3 - key1
         * */


        cache.put( "key7", "value7" );
        cache.put( "key8", "value8" );

        assertEquals( "value7", cache.get( "key7" ) );


        /*
         * since the cache size is at it's max size, adding a new cache entry will evict
         * cache entry with lowest frequency and least recently used
         * frequency 0 - key8,
         * frequency 1 - key3, key7,
         * frequency 2 - key6
         * frequency 3 - key1
         * key8 will be removed. and cache size will be 5(with new entry).
         * */

        cache.put( "key9", "value9" );
        assertEquals( null, cache.get( "key8" ) );
        assertEquals( 5, cache.cacheSize() );
        /*
         * frequency 0 - key9,
         * frequency 1 - key3, key7,
         * frequency 2 - key6
         * frequency 3 - key1
         * */

        assertEquals( "value1", cache.get( "key1" ) );
        assertEquals( "value3", cache.get( "key3" ) );
        assertEquals( "value6", cache.get( "key6" ) );
        assertEquals( "value7", cache.get( "key7" ) );
        assertEquals( "value9", cache.get( "key9" ) );

    }
}