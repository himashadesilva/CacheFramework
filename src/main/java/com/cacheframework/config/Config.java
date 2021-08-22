package com.cacheframework.config;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class Config
{
    private String cacheName;
    private String cacheType;
    private String evictionStrategy;
    private int levelOneCacheSize;
    private boolean levelTwoCacheEnable;
    private int levelTwoCacheSize;
    private String levelTwoCacheDir;

    public String getCacheName()
    {
        return cacheName;
    }

    public void setCacheName( String cacheName )
    {
        this.cacheName = cacheName;
    }

    public String getCacheType()
    {
        return cacheType;
    }

    public void setCacheType( String cacheType )
    {
        this.cacheType = cacheType;
    }

    public String getEvictionStrategy()
    {
        return evictionStrategy;
    }

    public void setEvictionStrategy( String evictionStrategy )
    {
        this.evictionStrategy = evictionStrategy;
    }

    public int getLevelOneCacheSize()
    {
        return levelOneCacheSize;
    }

    public void setLevelOneCacheSize( int levelOneCacheSize )
    {
        this.levelOneCacheSize = levelOneCacheSize;
    }

    public boolean isLevelTwoCacheEnable()
    {
        return levelTwoCacheEnable;
    }

    public void setLevelTwoCacheEnable( boolean levelTwoCacheEnable )
    {
        this.levelTwoCacheEnable = levelTwoCacheEnable;
    }

    public int getLevelTwoCacheSize()
    {
        return levelTwoCacheSize;
    }

    public void setLevelTwoCacheSize( int levelTwoCacheSize )
    {
        this.levelTwoCacheSize = levelTwoCacheSize;
    }

    public String getLevelTwoCacheDir()
    {
        return levelTwoCacheDir;
    }

    public void setLevelTwoCacheDir( String levelTwoCacheDir )
    {
        this.levelTwoCacheDir = levelTwoCacheDir;
    }

    @Override
    public String toString()
    {
        return "[cacheName=" + cacheName + ", cacheType=" + cacheType + ", evictionStrategy=" + evictionStrategy
                       + ", levelOneCacheSize=" + levelOneCacheSize + ", levelTwoCacheEnable=" + levelTwoCacheEnable + ", levelTwoCacheSize="
                       + levelTwoCacheSize + ", levelTwoCacheDir=" + levelTwoCacheDir +"]";
    }
}
