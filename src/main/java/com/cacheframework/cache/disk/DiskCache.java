package com.cacheframework.cache.disk;

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

public class DiskCache<K, V> implements IDiskCache<K,V>
{
    private static final int NUMBER_DISK_SHARDS = 10;
    private Config config;
    private IMemoryCache<K,DiskElementDescriptor> keyHash;
    private String dirPath;
    private String cacheName;
    private int cacheMaxSize;


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
        V value = null;
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
                value = Utils.deserialize( file );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void addToCache( K key, V value )
    {
        if( keyHash.getSize() == cacheMaxSize )
        {
            keyHash.getEvictKeys().forEach( this::delete );
        }
        try
        {
            String filePath = getPathToFile( key );
            keyHash.addToCache( key, new DiskElementDescriptor( filePath ) );
            Utils.serialize( filePath, value );
        }
        catch( Exception e )
        {
            e.printStackTrace();
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
                e.printStackTrace();
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
