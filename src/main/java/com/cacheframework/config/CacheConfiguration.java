package com.cacheframework.config;

import java.util.Map;

/**
 *  cache configurations Object
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class CacheConfiguration
{
    private Map<String,Config> caches;

    public Map<String,Config> getCaches()
    {
        return caches;
    }

    public void setCaches( Map<String,Config> caches )
    {
        this.caches = caches;
    }

    @Override
    public String toString()
    {
        return "Cache Config [caches= " + caches + "]";
    }
}

