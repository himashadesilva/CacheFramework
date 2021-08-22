package com.cacheframework.cache.composite;

import com.cacheframework.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
class CompositeCacheTest
{
    private CompositeCache<String,String> cache;

    @BeforeEach
    void setUp()
    {
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
    void LRUCacheTest() throws Exception
    {
        Config config = new Config();
        config.setCacheName( "TestCompositeCache" );
        config.setCacheType( "Composite" );
        config.setEvictionStrategy( "LRU" );
        config.setLevelOneCacheSize( 5 );
        config.setLevelTwoCacheDir( "src/main/resources/cacheTest" );
        config.setLevelTwoCacheSize( 5 );

        cache = new CompositeCache<>( config );
        cache.addToCache( "key1", "value1" );
        cache.addToCache( "key2", "value2" );
        cache.addToCache( "key3", "value3" );
        cache.addToCache( "key4", "value4" );
        cache.addToCache( "key5", "value5" );
        cache.addToCache( "key6", "value6" );
        cache.addToCache( "key7", "value7" );
        cache.addToCache( "key8", "value8" );
        cache.addToCache( "key9", "value9" );
        cache.addToCache( "key10", "value10" );

        /*
         * memory -> key 6,7,8,9,10
         * disk -> key 1,2,3,4,5
         * */

        assertEquals( "value1", cache.getFromCache( "key1" ) );
        assertEquals( 10, cache.getSize() );
        /*
         * memory -> key 7,8,9,10,1
         * disk -> key 2,3,4,5,6
         *
         * adding new cache entry will spool key7 into disk and key2 will be removed
         * from the disk.
         * */

        cache.addToCache( "key11", "value11" );
        /*
         * memory -> key 8,9,10,1,11
         * disk -> key 3,4,5,6,7
         * */
        assertEquals( null, cache.getFromCache( "key2" ) );
        assertEquals( 10, cache.getSize() );
    }

    @Test
    void LFUCacheTest() throws Exception
    {
        Config config = new Config();
        config.setCacheName( "TestCompositeCache" );
        config.setCacheType( "Composite" );
        config.setEvictionStrategy( "LFU" );
        config.setLevelOneCacheSize( 5 );
        config.setLevelTwoCacheDir( "src/main/resources/cacheTest" );
        config.setLevelTwoCacheSize( 5 );

        cache = new CompositeCache<>( config );
        cache.addToCache( "key1", "value1" );
        cache.addToCache( "key2", "value2" );
        cache.addToCache( "key3", "value3" );
        cache.addToCache( "key4", "value4" );
        cache.addToCache( "key5", "value5" );
        cache.addToCache( "key6", "value6" );
        cache.addToCache( "key7", "value7" );
        cache.addToCache( "key8", "value8" );
        cache.addToCache( "key9", "value9" );
        cache.addToCache( "key10", "value10" );
        /*
         * memory -> freq 0 - key 6,7,8,9,10
         * disk ->   freq 0 - key 1,2,3,4,5
         * */
        assertEquals( "value1", cache.getFromCache( "key1" ) );
        assertEquals( 10, cache.getSize() );

        /*
         * memory -> freq 0 - key 7,8,9,10,1
         * disk ->   freq 0 - key 2,3,4,5,6
         * */
        assertEquals( "value1", cache.getFromCache( "key1" ) );
        assertEquals( "value1", cache.getFromCache( "key1" ) );
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

        cache.addToCache( "key11", "value11" );

        /*
         * memory -> freq 0 - key 8,9,10,11
         *           freq 2 - key 1
         * disk ->   freq 0 - key 3,4,5,6,7,
         * */
        assertEquals( null, cache.getFromCache( "key2" ) );
        assertEquals( 10, cache.getSize() );

        assertEquals( "value7", cache.getFromCache( "key7" ) );
        /*
         * memory -> freq 0 - key 9,10,11,7
         *           freq 2 - key 1
         * disk ->   freq 0 - key 3,4,5,6,8
         * */
        assertEquals( "value3", cache.getFromCache( "key3" ) );
        /*
         * memory -> freq 0 - key 10,11,7,3
         *           freq 2 - key 1
         * disk ->   freq 0 - key 4,5,6,8,9
         * */

        cache.addToCache( "key12", "value12" );
        /*
         * memory -> freq 0 - key 11,7,3,12
         *           freq 2 - key 1
         * disk ->   freq 0 - key 5,6,8,9,12
         * */
        assertEquals( null, cache.getFromCache( "key4" ) );
        assertEquals( 10, cache.getSize() );
    }
}