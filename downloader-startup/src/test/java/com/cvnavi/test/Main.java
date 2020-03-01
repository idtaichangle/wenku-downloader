package com.cvnavi.test;

import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.browser.JxBrowserEngine;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        Browser browser=JxBrowserEngine.getEngine().newBrowser();
//        BrowserView browserView=BrowserView.newInstance(browser);
//        JFrame frame=new JFrame();
//        frame.setContentPane(browserView);
//        frame.setSize(1024,768);
//        frame.setVisible(true);
//        browser.navigation().loadUrl("http://ishare.iask.sina.com.cn/f/RgYkxBNY4x.html");

        BrowserFrame bf=BrowserFrame.instance();
        bf.showFrame();
        bf.browse("http://ishare.iask.sina.com.cn/f/RgYkxBNY4x.html");
    }
}
