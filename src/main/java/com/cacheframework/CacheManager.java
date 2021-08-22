package com.cacheframework;

import com.cacheframework.cache.ICache;
import com.cacheframework.cache.composite.CompositeCache;
import com.cacheframework.cache.memory.lfu.LFUMemoryCache;
import com.cacheframework.cache.memory.lru.LRUMemoryCache;
import com.cacheframework.config.CacheConfiguration;
import com.cacheframework.config.Config;
import com.cacheframework.exception.CacheConfigException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

public class CacheManager
{
    private static CacheManager instance;
    private final Hashtable<String,ICache> cachePool = new Hashtable<>();
    private CacheConfiguration cacheConfiguration;

    private CacheManager()
    {
        init();
    }

    public static CacheManager getInstance()
    {
        if( instance == null )
        {
            instance = new CacheManager();
        }
        return instance;
    }

    private void init()
    {
        Yaml yaml = new Yaml();
        try( InputStream in = Files.newInputStream( Paths.get( "src/main/resources/cacheConfig.yaml" ) ) )
        {
            cacheConfiguration = yaml.loadAs( in, CacheConfiguration.class );
            System.out.println( cacheConfiguration.toString() );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    public <K, V> ICache<K,V> getCache( String cacheName ) throws CacheConfigException
    {
        if( !cachePool.contains( cacheName ) )
        {
            ICache<K,V> cache = null;
            if( cacheConfiguration.getCaches().containsKey( cacheName ) )
            {
                Config config = cacheConfiguration.getCaches().get( cacheName );
                if( "Memory".equals( config.getCacheType() ) )
                {
                    if( "LRU".equals( config.getEvictionStrategy() ) )
                    {
                        cache = new LRUMemoryCache<>( config.getCacheName(), config.getLevelOneCacheSize() );
                    }
                    else if( "LFU".equals( config.getEvictionStrategy() ) )
                    {
                        cache = new LFUMemoryCache<>( config.getCacheName(), config.getLevelOneCacheSize() );
                    }
                }
                else if( "Composite".equals( config.getCacheType() ) )
                {
                    cache = new CompositeCache<>( config );
                }
                else
                {
                    throw new CacheConfigException( "Cache configurations are not setup Properly for cache : " + cacheName );
                }
                cachePool.put( cacheName, cache );
                return cache;
            }
            else
            {
                throw new CacheConfigException( "Cache configurations are not setup Properly." );
            }
        }
        else
        {
            return cachePool.get( cacheName );
        }
    }

    public void shutdown()
    {
        cachePool.forEach( ( s, iCache ) ->
        {
            iCache.clearCache();
        } );
    }

}
