package com.cvnavi.downloader;

import com.cvnavi.downloader.util.ResourceReader;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Log4j2
public class Config {
    public static String TMP_DIR =null;

    public static String FILES_DIR =null;

    public static Boolean PDF_OCR=true;

    static{
        init();
    }

    static void init(){
        if(System.getProperty("APP_HOME")!=null){
            FILES_DIR =System.getProperty("APP_HOME")+ File.separator+"files";
            TMP_DIR =System.getProperty("APP_HOME")+ File.separator+"tmp";
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
            Properties p= ResourceReader.readProperties("application.properties");
            PDF_OCR=Boolean.parseBoolean(p.getProperty("pdf.ocr"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
