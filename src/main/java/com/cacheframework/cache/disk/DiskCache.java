package com.cacheframework.cache.disk;

import com.cacheframework.cache.CacheElement;
import com.cacheframework.cache.memory.IMemoryCache;
import com.cacheframework.cache.memory.lfu.LFUMemoryCache;
import com.cacheframework.cache.memory.lru.LRUMemoryCache;
import com.cacheframework.cache.util.Utils;
import com.cacheframework.config.Config;
import com.cacheframework.exception.CacheConfigException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Disk cache implementation. Cache configurations like eviction strategy, disk directory path, cache max size are
 * taken from {@link Config}. Here Cache key are stored in memory and values will be written into disk. {@link IMemoryCache}
 * is used to keep cache keys. Memory cache stores {@link DiskElementDescriptor} which contains file path for the cached object.
 *
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 * @param <K> cache key Type
 * @param <V> cache value Type
 */
public class DiskCache<K, V> implements IDiskCache<K,V>
{
    private static final int NUMBER_DISK_SHARDS = 10;
    private Config config;
    private IMemoryCache<K,DiskElementDescriptor> keyHash;
    private String dirPath;
    private String cacheName;
    private int cacheMaxSize;
    private Logger logger =  Logger.getLogger(this.getClass().getName());

    public DiskCache( Config config ) throws CacheConfigException
    {
        this.config = config;
        this.dirPath = config.getLevelTwoCacheDir();
        this.cacheMaxSize = config.getLevelTwoCacheSize();
        this.cacheName = config.getCacheName();
        init();
    }

    private void init() throws CacheConfigException
    {
        File dir = new File( this.dirPath );
        if( dir.exists() && dir.isFile() )
        {
            throw new CacheConfigException( "data dir: " + this.dirPath + ", is a file, should be a directory" );
        }
        if( !dir.exists() )
        {
            dir.mkdirs();
        }

        for( int i = 0; i < NUMBER_DISK_SHARDS; i++ )
        {
            File shardDir = new File( dir, Integer.toString( i ) );
            if( shardDir.exists() && shardDir.isDirectory() )
            {
                continue;
            }
            if( shardDir.exists() && ( !shardDir.isDirectory() ) )
            {
                shardDir.delete();
            }
            shardDir.mkdir();
        }

        if( "LRU".equals( config.getEvictionStrategy() ) )
        {
            keyHash = new LRUMemoryCache<>( config.getCacheName(), config.getLevelTwoCacheSize() );
        }
        else if( "LFU".equals( config.getEvictionStrategy() ) )
        {
            keyHash = new LFUMemoryCache<>( config.getCacheName(), config.getLevelTwoCacheSize() );
        }
        else
        {
            throw new CacheConfigException("Cache Config not setup properly for :"+ cacheName);
        }
    }

    @Override
    public V getFromCache( K key )
    {
        CacheElement<K,V> cacheElement = null;
        try
        {
            DiskElementDescriptor elementDescriptor = keyHash.getFromCache( key );
            if( elementDescriptor != null )
            {
                File file = new File( elementDescriptor.getFilePath() );
                if( !file.exists() )
                {
                    keyHash.delete( key );
                    return null;
                }
                cacheElement = Utils.deserialize( file );
            }
        }
        catch( Exception e )
        {
            logger.log( Level.SEVERE, "Error in deserializing Object. Key : {0}", new Object[]{key} );
        }
        return cacheElement == null ? null :cacheElement.getValue();
    }

    @Override
    public void addToCache( K key, V value )
    {
        CacheElement<K,V> cacheElement = new CacheElement<>( key, value, cacheName );
        if( keyHash.getSize() == cacheMaxSize )
        {
            keyHash.getEvictKeys().forEach( this::delete );
        }
        try
        {
            String filePath = getPathToFile( key );
            keyHash.addToCache( key, new DiskElementDescriptor( filePath ) );
            Utils.serialize( filePath, cacheElement );
        }
        catch( Exception e )
        {
            logger.log( Level.SEVERE, "Error in Serializing Object. Key : {0}", new Object[]{key} );
        }
    }

    @Override
    public void delete( K key )
    {
        if( keyHash.getFromCache( key ) != null )
        {
            try
            {
                keyHash.delete( key );
                Files.delete( Path.of( getPathToFile( key ) ) );
            }
            catch( IOException | NoSuchAlgorithmException e )
            {
                logger.log( Level.SEVERE, "Error in deleting Key. Key : {0}", new Object[]{key} );
            }
        }
    }

    @Override
    public int getSize()
    {
        return keyHash.getSize();
    }

    @Override
    public void clearCache()
    {
        File folder = new File( dirPath );
        Utils.deleteFolder( folder );
        keyHash.clearCache();
    }

    public String getPathToFile( K key ) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        return this.dirPath + "/" + ( Math.abs( key.hashCode() ) % NUMBER_DISK_SHARDS ) + "/" + Utils.sha256( key );
    }


}
