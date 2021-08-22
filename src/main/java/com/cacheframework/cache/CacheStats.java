package com.cacheframework.cache;

public class CacheStats
{
    private int hitCount;
    private int missCount;
    private int totalPutCount;

    public CacheStats()
    {
        //constructor
    }

    public int getHitCount()
    {
        return hitCount;
    }

    public void incHitCount(  )
    {
        this.hitCount++;
    }

    public int getMissCount()
    {
        return missCount;
    }

    public void incMissCount( )
    {
        this.missCount++;
    }

    public int getTotalPutCount()
    {
        return totalPutCount;
    }

    public void incTotalPutCount(  )
    {
        this.totalPutCount++;
    }
}
