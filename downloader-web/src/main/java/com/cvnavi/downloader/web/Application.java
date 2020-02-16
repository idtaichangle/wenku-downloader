package com.cvnavi.downloader.web;

import com.cvnavi.downloader.browser.BrowserFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

    public static String DOWNLOADER_HOME;
    static {
        setHome();
    }

    public static void setHome(){
        if(System.getProperty("DOWNLOADER_HOME")!=null){
            DOWNLOADER_HOME=System.getProperty("DOWNLOADER_HOME");
        }else{
            String file=Application.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            if(file.endsWith(".jar")){
                DOWNLOADER_HOME=new File(file).getParentFile().getParent();
            }else if(file.endsWith("/classes/")){
                DOWNLOADER_HOME=new File(file).getParent();
            }
        }
        System.setProperty("DOWNLOADER_HOME",DOWNLOADER_HOME);
    }


    public static void main(String[] args) {
        BrowserFrame.instance().showFrame();
        SpringApplication.run(Application.class);
    }
}
