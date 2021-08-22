# CacheFramework
Caching framework to create in memory and disk cache with LRU, LFU eviction strategy.
Yaml configuration file is used to configure caches. 

```yaml
caches:
  lruMemCache:
    cacheName: lruMemCache
    cacheType: Memory
    evictionStrategy: LRU
    levelOneCacheSize: 5
  lfuMemCache:
    cacheName: lfuMemCache
    cacheType: Memory
    evictionStrategy: LFU
    levelOneCacheSize: 5
  lruCompositeCache:
    cacheName: lruCompositeCache
    cacheType: Composite
    evictionStrategy: LRU
    levelOneCacheSize: 5
    levelTwoCacheEnable: true
    levelTwoCacheSize: 5
    levelTwoCacheDir: "src/main/resources/cacheTest/lruCompositeCache"
```

Cache store type can be given from cacheType.
```
Memory    - In memory cache
Disk      - Disk Cache
Composite - both in memory and disk cache (L1 memory and L2 Disk)
```

Cache key eviction strategy can be given from evictionStrategy
```
LRU   - Least Recently Used keys will be evicted
LFU   - Least Frequently Used keys will be evicted
```

Caches can be created using cache config name. 
```java
public class LFUCompositeCache extends AbstractCache<String, String>
{
    private static LFUCompositeCache instance;

    private LFUCompositeCache(  ) throws CacheConfigException
    {
        super( "lfuCompositeCache" );
    }


    public static LFUCompositeCache getInstance() throws CacheConfigException
    {
        if( instance == null )
        {
            instance = new LFUCompositeCache();
        }
        return instance;
    }
}
```

Usage :
```java
void cacheTest() throws CacheConfigException
    {
        //mem cache max size is set to 5 in cacheConfig yaml
        //disk cache max size is set to 5 in cacheConfig yaml
        LFUCompositeCache cache = LFUCompositeCache.getInstance();
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
      }
```



