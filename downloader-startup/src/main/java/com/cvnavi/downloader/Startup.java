package com.cvnavi.downloader;

import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.db.DefaultDbChecker;
import com.cvnavi.downloader.util.ResourceReader;
import com.cvnavi.downloader.web.Application;

import java.io.File;

public class Startup {

    public static String DOWNLOADER_HOME;
    static {
        setHome();
    }

    public static void setHome(){
        if(System.getProperty("DOWNLOADER_HOME")!=null){
            DOWNLOADER_HOME=System.getProperty("DOWNLOADER_HOME");
        }else{
            String file=Startup.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            if(file.endsWith(".jar")){
                DOWNLOADER_HOME=new File(file).getParentFile().getParent();
            }else if(file.endsWith("/classes/")){
                DOWNLOADER_HOME=new File(file).getParent();
            }
        }
        System.setProperty("DOWNLOADER_HOME",DOWNLOADER_HOME);
    }

    public static void main(String[] args) {
        new DefaultDbChecker().checkTable();
        BrowserFrame.instance().showFrame();
        Application.main(args);
    }
}
