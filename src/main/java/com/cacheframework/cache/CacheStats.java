package com.cacheframework.cache;

/**
 * Cache Stats object to collect cache stats.
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
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
