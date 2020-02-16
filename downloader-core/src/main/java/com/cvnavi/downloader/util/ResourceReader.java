package com.cvnavi.downloader.util;

import com.cvnavi.downloader.base.AbstractDownloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceReader {
    public static String read(String file){
        if(file!=null){
            String str="";
            InputStream is= AbstractDownloader.class.getClassLoader().getResourceAsStream(file);
            if(is!=null){
                try(BufferedReader reader=new BufferedReader(new InputStreamReader(is))){
                    String line=null;
                    while((line=reader.readLine())!=null){
                        str+=line;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            return str;
        }else{
            return null;
        }
    }
}
