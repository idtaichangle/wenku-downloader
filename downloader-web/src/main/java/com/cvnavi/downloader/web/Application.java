package com.cvnavi.downloader.web;

import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.util.ResourceReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Properties;

@SpringBootApplication
public class Application {
    public static Properties SYSTEM_PROPERTIES=new Properties();

    static {
        SYSTEM_PROPERTIES= ResourceReader.readProperties("application.properties");
    }

    public static void main(String[] args) {
        SpringApplication application=new SpringApplication(Application.class);
        application.setDefaultProperties(SYSTEM_PROPERTIES);
        application.run(args);
    }
}
