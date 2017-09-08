package com.wazesounds;
import android.util.Log; 
import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream; 
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream; 
 
 
public class Compress 

{ 
  private static final int BUFFER = 2048; 
 
  private String[] _files; 
  private String _zipFile; 
 
  public Compress(String[] files, String zipFile) { 
    _files = files; 
    _zipFile = zipFile; 
  } 
 
  public void zip() { 
    try  { 
      BufferedInputStream origin = null; 
      FileOutputStream dest = new FileOutputStream(_zipFile); 
 
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
 
      byte data[] = new byte[BUFFER]; 
 
      for(int i=0; i < _files.length; i++) { 
        Log.v("Compress", "Adding: " + _files[i]); 
        FileInputStream fi = new FileInputStream(_files[i]); 
        origin = new BufferedInputStream(fi, BUFFER); 
        ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1)); 
        out.putNextEntry(entry); 
        int count; 
        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
          out.write(data, 0, count); 
        } 
        origin.close(); 
      } 
 
      out.close(); 
    } catch(Exception e) { 
      e.printStackTrace(); 
    } 
 
  } 
  
  
  public void getZipFiles(String filename)
  {
      try
      {
          String destinationname = "d:\\servlet\\testZip\\";
          byte[] buf = new byte[1024];
          ZipInputStream zipinputstream = null;
          ZipEntry zipentry;
          zipinputstream = new ZipInputStream(
              new FileInputStream(filename));

          zipentry = zipinputstream.getNextEntry();
          while (zipentry != null) 
          { 
              //for each entry to be extracted
              String entryName = zipentry.getName();
              System.out.println("entryname "+entryName);
              int n;
              FileOutputStream fileoutputstream;
              File newFile = new File(entryName);
              String directory = newFile.getParent();
              
              if(directory == null)
              {
                  if(newFile.isDirectory())
                      break;
              }
              
              fileoutputstream = new FileOutputStream(
                 destinationname+entryName);             

              while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
                  fileoutputstream.write(buf, 0, n);

              fileoutputstream.close(); 
              zipinputstream.closeEntry();
              zipentry = zipinputstream.getNextEntry();

          }//while

          zipinputstream.close();
      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
  }

 
} 