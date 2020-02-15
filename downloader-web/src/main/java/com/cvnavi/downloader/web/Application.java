package com.cvnavi.downloader.web;

import com.cvnavi.downloader.BrowserFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static BrowserFrame browser=new BrowserFrame();

    public static void main(String[] args) {
        browser.showGUI(false);
        SpringApplication.run(Application.class);
    }
}
