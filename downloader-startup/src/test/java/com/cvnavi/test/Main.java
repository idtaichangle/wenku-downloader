package com.cvnavi.test;

import com.cvnavi.downloader.Startup;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.browser.JxBrowserEngine;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Startup.setHome();


        BrowserFrame bf=BrowserFrame.instance();
        bf.showFrame();
        bf.browse("http://ishare.iask.sina.com.cn/f/RgYkxBNY4x.html");
    }
}
