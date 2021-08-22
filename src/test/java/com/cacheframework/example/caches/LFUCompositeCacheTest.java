package com.cacheframework.example.caches;

import com.cacheframework.exception.CacheConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LFUCompositeCacheTest
{
    private LFUCompositeCache cache;
    @BeforeEach
    void setUp() throws CacheConfigException
    {
        cache = LFUCompositeCache.getInstance();
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    void cacheTest()
    {
        //mem cache max size is set to 5 in cacheConfig yaml
        //disk cache max size is set to 5 in cacheConfig yaml
        cache.put( "key1", "value1" );
        cache.put( "key2", "value2" );
        cache.put( "key3", "value3" );
        cache.put( "key4", "value4" );
        cache.put( "key5", "value5" );
        cache.put( "key6", "value6" );
        cache.put( "key7", "value7" );
        cache.put( "key8", "value8" );
        cache.put( "key9", "value9" );
        cache.put( "key10", "value10" );
        /*
         * memory -> freq 0 - key 6,7,8,9,10
         * disk ->   freq 0 - key 1,2,3,4,5
         * */
        assertEquals( "value1", cache.get( "key1" ) );
        assertEquals( 10, cache.cacheSize() );

        /*
         * memory -> freq 0 - key 7,8,9,10,1
         * disk ->   freq 0 - key 2,3,4,5,6
         * */
        assertEquals( "value1", cache.get( "key1" ) );
        assertEquals( "value1", cache.get( "key1" ) );
        /*
         * memory -> freq 0 - key 7,8,9,10
         *           freq 2 - key 1
         * disk ->   freq 0 - key 2,3,4,5,6
         *
         * adding new cache entry will spool lowest frequency cache entry from memory
         * which are key 7,8,9,10 and only 7 will spool into disk since it is least recently used.
         * since the disk cache is also full, disk cache will remove
         * lowest frequency  key 2
         * */

        cache.put( "key11", "value11" );

        /*
         * memory -> freq 0 - key 8,9,10,11
         *           freq 2 - key 1
         * disk ->   freq 0 - key 3,4,5,6,7,
         * */
        assertEquals( null, cache.get( "key2" ) );
        assertEquals( 10, cache.cacheSize() );

        assertEquals( "value7", cache.get( "key7" ) );
        /*
         * memory -> freq 0 - key 9,10,11,7
         *           freq 2 - key 1
         * disk ->   freq 0 - key 3,4,5,6,8
         * */
        assertEquals( "value3", cache.get( "key3" ) );
        /*
         * memory -> freq 0 - key 10,11,7,3
         *           freq 2 - key 1
         * disk ->   freq 0 - key 4,5,6,8,9
         * */

        cache.put( "key12", "value12" );
        /*
         * memory -> freq 0 - key 11,7,3,12
         *           freq 2 - key 1
         * disk ->   freq 0 - key 5,6,8,9,12
         * */
        assertEquals( null, cache.get( "key4" ) );
        assertEquals( 10, cache.cacheSize() );

    }
}