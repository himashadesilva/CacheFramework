package com.cacheframework.example.caches;

import com.cacheframework.exception.CacheConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
class LRUCompositeCacheTest
{
    private LRUCompositeCache cache;
    @BeforeEach
    void setUp() throws CacheConfigException
    {
        cache = LRUCompositeCache.getInstance();
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
         * memory -> key 6,7,8,9,10
         * disk -> key 1,2,3,4,5
         * */

        assertEquals( "value1", cache.get( "key1" ) );
        assertEquals( 10, cache.cacheSize() );
        /*
         * memory -> key 7,8,9,10,1
         * disk -> key 2,3,4,5,6
         *
         * adding new cache entry will spool key7 into disk and key2 will be removed
         * from the disk.
         * */

        cache.put( "key11", "value11" );
        /*
         * memory -> key 8,9,10,1,11
         * disk -> key 3,4,5,6,7
         * */
        assertEquals( null, cache.get( "key2" ) );
        assertEquals( 10, cache.cacheSize() );

        assertEquals( "value9", cache.get( "key9" ) );
        assertEquals( "value8", cache.get( "key8" ) );
        /*
         * memory -> key 10,1,11,9,8
         * disk -> key 3,4,5,6,7
         * */

        cache.put( "key12", "value12" );
        /*
         * memory -> key 1,11,9,8,12
         * disk -> key 4,5,6,7,10
         * */
        assertEquals( null, cache.get( "key3" ) );

        assertEquals( "value7", cache.get( "key7" ) );
        assertEquals( "value6", cache.get( "key6" ) );
        /*
         * memory -> key 9,8,12,7,6
         * disk -> key 4,5,10,1,11
         * */
        cache.put( "key13", "value13" );
        /*
         * memory -> key 8,12,7,6,13
         * disk -> key 5,10,1,11,9
         * */
        assertEquals( null, cache.get( "key4" ) );
    }
}