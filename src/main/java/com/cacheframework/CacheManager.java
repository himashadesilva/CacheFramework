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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cache Manager to handle cache initialization. cache configurations are taken from a yaml file.
 * initialzed caches are stored in a cache pool.
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class CacheManager
{
    private static CacheManager instance;
    private final Hashtable<String,ICache> cachePool = new Hashtable<>();
    private CacheConfiguration cacheConfiguration;
    private Logger logger = Logger.getLogger( this.getClass().getName() );

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
            logger.log( Level.INFO, "Cache config: {0}", cacheConfiguration );
        }
        catch( IOException e )
        {
            logger.log( Level.SEVERE, "Error in reading cacheConfig yaml", e );
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
