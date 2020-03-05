package com.cvnavi.downloader.base;

import java.awt.image.BufferedImage;

/**
 * 下载https://max.book118.com/的文档
 */
public class Book118Downloader extends AbstractDownloader {
    public Book118Downloader(){
        prepareJsFile="js/book118.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "max.book118.com"
        };
    }

    protected void scrollPage(int i) throws InterruptedException {
        float scroll=i==0?0:windowHeight;;
        executeJavaScriptAsync("$('iframe:last')[0].contentWindow.scrollBy(0,"+scroll+")");
        Thread.sleep(100);
    }
}
