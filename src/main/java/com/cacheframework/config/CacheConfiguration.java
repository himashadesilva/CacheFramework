package com.cacheframework.config;

import java.util.Map;

public class CacheConfiguration
{
    private Map< String,Config> caches;

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
        return super.toString();
    }
}

