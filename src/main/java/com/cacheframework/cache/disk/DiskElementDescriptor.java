package com.cacheframework.cache.disk;

public class DiskElementDescriptor
{

    private String filePath;
    private int frequency;

    public DiskElementDescriptor( String filePath )
    {
        this.filePath = filePath;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void setFrequency( int frequency )
    {
        this.frequency = frequency;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath( String filePath )
    {
        this.filePath = filePath;
    }
}
