package com.cacheframework;

import com.cacheframework.exception.CacheConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheManagerTest
{

    @BeforeEach
    void setUp()
    {
    }

    @AfterEach
    void tearDown()
    {
        CacheManager.getInstance().shutdown();
    }

    @Test
    void getInstance()
    {
        CacheManager.getInstance();
    }

    @Test
    void getCache() throws CacheConfigException
    {
        CacheManager.getInstance().getCache( "lruMemCache" );
        CacheManager.getInstance().getCache( "lfuMemCache" );
        CacheManager.getInstance().getCache( "lfuCompositeCache" );
        CacheManager.getInstance().getCache( "lruCompositeCache" );
    }
}