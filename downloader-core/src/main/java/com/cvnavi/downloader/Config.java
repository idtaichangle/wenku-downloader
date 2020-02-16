package com.cvnavi.downloader;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class Config {
    public static String TMP_DIR =null;

    public static String FILES_DIR =null;

    static{
        init();
    }

    static void init(){
        if(System.getProperty("DOWNLOADER_HOME")!=null){
            FILES_DIR =System.getProperty("DOWNLOADER_HOME")+ File.separator+"files";
            TMP_DIR =System.getProperty("DOWNLOADER_HOME")+ File.separator+"tmp";
        }else{
            FILES_DIR =System.getProperty("java.io.tmpdir")+ File.separator+"wenku-downloader";
            TMP_DIR =System.getProperty("java.io.tmpdir")+ File.separator+"wenku-downloader"+File.separator+"tmp";
        }
        try {
            if(!Files.exists(Paths.get(FILES_DIR))){
                Files.createDirectories(Paths.get(FILES_DIR));
            }

            if(!Files.exists(Paths.get(TMP_DIR))){
                Files.createDirectories(Paths.get(TMP_DIR));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
