package com.cvnavi.downloader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Book118Downloader extends AbstractDownloader {
    @Override
    public String getDocType() {
        return null;
    }

    @Override
    public String getPageName() {
        String value=browser.mainFrame().get().executeJavaScript("doc_title");
        if(value!=null){
            name=value;
        }
        return name;
    }

    @Override
    public int getPageCount() {
        String value=browser.mainFrame().get().executeJavaScript("$('#pagenumber').text();");
        if(value!=null && value.length()>0){
            totalPage=Integer.parseInt(value.trim().replace("页",""));
        }
        return totalPage;
    }

    @Override
    public void download() throws Exception {
        getPageCount();
        getPageName();
        getDocType();

        prepareDownload("book118.js");

        Thread.sleep(8000);
        String script="$('#btn_read').click()";
        browser.mainFrame().get().executeJavaScript(script);
        Thread.sleep(3000);

        for(int p=1;p<=totalPage;p++){
            browser.mainFrame().get().executeJavaScript("$('div[data-id="+p+"]')[0].scrollIntoView();");
            script="$('div[data-id="+p+"] img').prop('src')";
            String url=browser.mainFrame().get().executeJavaScript(script);
            if(url!=null && url.length()>0){
                BufferedImage pageImage= ImageIO.read(new URL(url));
                writePageImage(pageImage,p);
            }
            Thread.sleep(1000);
        }

        writePdf();
    }
}
