package com.cacheframework.cache.disk;

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
class DiskCacheTest
{
    private DiskCache<String,String> cache;

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
        config.setCacheName( "TestDiskCache" );
        config.setCacheType( "Disk" );
        config.setEvictionStrategy( "LRU" );
        config.setLevelTwoCacheDir( "src/main/resources/cacheTest" );
        config.setLevelTwoCacheSize( 5 );

        cache = new DiskCache<>( config );

        cache.addToCache( "key1", "value1" );
        cache.addToCache( "key2", "value2" );
        cache.addToCache( "key3", "value3" );
        cache.addToCache( "key4", "value4" );
        cache.addToCache( "key5", "value5" );

        assertEquals( "value1", cache.getFromCache( "key1" ) );

        cache.addToCache( "key6", "value6" );

        assertEquals( null, cache.getFromCache( "key2" ) );
        assertEquals( 5, cache.getSize() );

        cache.addToCache( "key7", "value7" );
        assertEquals( null, cache.getFromCache( "key3" ) );
    }

    @Test
    void LFUCacheTest() throws Exception
    {
        Config config = new Config();
        config.setCacheName( "TestDiskCache" );
        config.setCacheType( "Disk" );
        config.setEvictionStrategy( "LFU" );
        config.setLevelTwoCacheDir( "src/main/resources/cacheTest" );
        config.setLevelTwoCacheSize( 5 );

        cache = new DiskCache<>( config );

        cache.addToCache( "key1", "value1" );
        cache.addToCache( "key2", "value2" );
        cache.addToCache( "key3", "value3" );
        cache.addToCache( "key4", "value4" );
        cache.addToCache( "key5", "value5" ); //cache size is reach to it's max size

        assertEquals( "value1", cache.getFromCache( "key1" ) ); //key1 frequency is now 1.
        assertEquals( "value3", cache.getFromCache( "key3" ) ); //key3 frequency is now 1.


        /*
         * since the cache size is at it's max size, adding a new cache entry will evict
         * cache entry with lowest frequency and least recently used.
         * frequency 0 - key2, key4, key5
         * frequency 1 - key1, key3
         * key2 will be removed. and cache size will be 5(with new entry). (key4, key5,key1, key3, key6)
         * */
        cache.addToCache( "key6", "value6" );

        /*
         * frequency 0 - key4, key5, key6
         * frequency 1 - key1, key3
         * */
        assertEquals( null, cache.getFromCache( "key2" ) );
        assertEquals( 5, cache.getSize() );


        assertEquals( "value6", cache.getFromCache( "key6" ) );
        assertEquals( "value6", cache.getFromCache( "key6" ) );
        assertEquals( "value1", cache.getFromCache( "key1" ) );
        assertEquals( "value1", cache.getFromCache( "key1" ) );

        /*
         * frequency 0 - key4, key5,
         * frequency 1 - key3
         * frequency 2 - key6
         * frequency 3 - key1,
         * */

        cache.addToCache( "key7", "value7" );
        cache.addToCache( "key8", "value8" );

        /*
         * frequency 0 - key7, key8,
         * frequency 1 - key3
         * frequency 2 - key6
         * frequency 3 - key1,
         * */

        assertEquals( "value7", cache.getFromCache( "key7" ) );
        /*
         * frequency 0 - key8
         * frequency 1 - key3, key7,
         * frequency 2 - key6,
         * frequency 3 - key1,
         * key8 will be removed. and cache size will be 5(with new entry). (key9,key3, key7, key6, key1)
         * */

        cache.addToCache( "key9", "value9" );
        assertEquals( null, cache.getFromCache( "key8" ) );
        assertEquals( 5, cache.getSize() );
    }

}