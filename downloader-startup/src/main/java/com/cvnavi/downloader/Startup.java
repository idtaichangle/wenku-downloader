package com.cvnavi.downloader;

import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.db.DefaultDbChecker;
import com.cvnavi.downloader.util.ResourceReader;
import com.cvnavi.downloader.web.Application;

import java.io.File;

public class Startup {

    public static String APP_HOME;
    static {
        setHome();
    }


    public static void setHome(){
        if(System.getProperty("APP_HOME")!=null){
            APP_HOME =System.getProperty("APP_HOME");
        }else{
            String file= Startup.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            if(file.endsWith(".jar")){
                APP_HOME =new File(file).getParentFile().getParent();
            }else if(file.endsWith("/classes/")){
                APP_HOME =new File(file).getParent();
            }
        }
        System.setProperty("APP_HOME", APP_HOME);
        System.setProperty("user.dir", APP_HOME);
    }

    public static void main(String[] args) {
        new DefaultDbChecker().checkTable();
        BrowserFrame.instance().showFrame();
        Application.main(args);
    }
}
