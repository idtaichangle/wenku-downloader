package com.cvnavi.downloader;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class MbalibDownloader extends AbstractDownloader {
    String docType;

    @Override
    public String getDocType() {
        String value=browser.mainFrame().get().executeJavaScript("wgDocType");
        if(value!=null){
            docType=value;
        }
        return docType;
    }

    @Override
    public String getPageName() {
        String value=browser.mainFrame().get().executeJavaScript("wgDocTitle");
        if(value!=null){
            name=value;
        }
        return name;
    }

    @Override
    public int getPageCount() {
        if(totalPage==0){
            String value=browser.mainFrame().get().executeJavaScript("$(\".num span\").text();");
            if(value!=null && value.length()>0){
                totalPage=Integer.parseInt(value.replace("/",""));
            }
        }
        return totalPage;
    }

    @Override
    public void download() throws Exception {
        getPageCount();
        getPageName();
        getDocType();

        prepareDownload("mbalib.js");

        pageWidth=getJsFloat("$(\"#page0\").width()");
        pageHeight=getJsFloat("$(\"#page0\").height()");
        pageLeftMargin=getJsFloat("$(\"#page0\").offset().left");

        for(int p=1;p<=totalPage;p++){
            browser.mainFrame().get().executeJavaScript("$(\"#page"+(p-1)+"\")[0].scrollIntoView()");
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
