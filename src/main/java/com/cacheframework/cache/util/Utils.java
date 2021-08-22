package com.cacheframework.cache.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Himasha de Silva
 * @since 22 AUG 2021
 */
public class Utils
{
    private Utils()
    {
    }

    public static <K> String sha256( K key ) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        byte[] digest = MessageDigest.getInstance( "SHA-256" ).digest( key.toString().getBytes( "UTF-8" ) );
        StringBuilder result = new StringBuilder();
        for( byte byt : digest )
        {
            String substring = Integer.toString( ( byt & 0xff ) + 0x100, 16 ).substring( 1 );
            result.append( substring );
        }
        return result.toString();
    }

    public static <T> T deserialize( File f ) throws Exception
    {
        try( FileInputStream fis = new FileInputStream( f );
             ObjectInputStream ois = new ObjectInputStream( fis ) )
        {
            return ( ( T ) ois.readObject() );
        }
        catch( Exception e )
        {
            f.delete();
            throw new Exception( "File Deserialization Failed" );
        }
    }

    public static  <T> void serialize( String path, T t ) throws Exception
    {

        try( FileOutputStream fos = new FileOutputStream( path, false );
             ObjectOutputStream oos = new ObjectOutputStream( fos ) )
        {
            oos.writeObject( t );
        }
        catch( Exception e )
        {
            throw new Exception( "Unable to serialize Object", e );
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}

