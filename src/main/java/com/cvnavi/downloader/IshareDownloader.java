package com.cvnavi.downloader;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class IshareDownloader extends AbstractDownloader {
    @Override
    public String getDocType() {
        return null;
    }

    @Override
    public String getPageName() {

        String value=browser.mainFrame().get().executeJavaScript("$('.crumb strong').text();");
        if(value!=null){
            name=value;
        }
        return name;
    }

    @Override
    public int getPageCount() {
        if(totalPage==0){
            String value=browser.mainFrame().get().executeJavaScript("$('.page-input-con span').text();");
            if(value!=null){
                totalPage=Integer.parseInt(value);
            }
        }
        return totalPage;
    }

    @Override
    public void download() throws Exception {
        getPageCount();
        getPageName();
        getDocType();

        prepareDownload("ishare.js");

        pageWidth=getJsFloat("$(\"div[data-num='1']\").width()");
        pageHeight=getJsFloat("$(\"div[data-num='1']\").height()");
        pageLeftMargin=getJsFloat("$(\"div[data-num='1']\").offset().left");

        for(int p=1;p<=totalPage;p++){
            browser.mainFrame().get().executeJavaScript("$(\"div[data-num='"+p+"']\")[0].scrollIntoView();");
            Thread.sleep(3000);

            BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

            int segment=(int)Math.ceil(pageHeight/windowHeight);

            for(int i=0;i<segment;i++){
                final float scroll=i==0?0:windowHeight;;
                SwingUtilities.invokeLater(()->{
                    browser.mainFrame().get().executeJavaScript("window.scrollBy(0,"+scroll+")");
                });
                Thread.sleep(500);
                snapshot(pageImage,i);
            }
            writePageImage(pageImage,p);
        }

        writePdf();
    }
}
