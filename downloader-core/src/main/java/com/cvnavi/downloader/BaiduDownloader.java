package com.cvnavi.downloader;


import com.teamdev.jxbrowser.view.swing.BitmapUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 下载baidu wenku的文档
 */
public class BaiduDownloader extends AbstractDownloader {

    private String script="";

    @Override
    public void download() throws Exception {
        getPageCount();
        getPageName();
        getDocType();

        prepareDownload("baidu.js");

        if(docType.contains("ppt")){
            downloadPpt();
        }else{
            downloadDoc();
        }

        writePdf();
    }

    private void downloadDoc() throws InterruptedException, IOException {

        pageWidth=getJsFloat("$('.reader-page-1').width()");
        pageHeight=getJsFloat("$('.reader-page-1').height()");
        pageLeftMargin=getJsFloat("$('.reader-page-1').offset().left");

        Thread.sleep(500);

        for(int p=1;p<=totalPage;p++){
            BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

            script="document.getElementsByClassName('reader-page-"+p+"')[0].scrollIntoView();";
            SwingUtilities.invokeLater(()->{
                browser.mainFrame().get().executeJavaScript(script);
            });
            Thread.sleep(2000);

            int segment=(int)Math.ceil(pageHeight/windowHeight);
            for(int i=0;i<segment;i++){
                final int scroll= (i==0?0: (int) windowHeight);

                SwingUtilities.invokeLater(()->{
                    browser.mainFrame().get().executeJavaScript("window.scrollBy(0,"+scroll+")");
                });

                Thread.sleep(1000);

                snapshot(pageImage,i);
            }
            writePageImage(pageImage,p);
        }
    }

    public String getDocType(){
        String value=browser.mainFrame().get().executeJavaScript(" window.__fisData._data.WkInfo.DocInfo.docType");
        if(value!=null){
            docType= value;
        }
        return docType;
    }

    public  String getPageName(){
        String  value=browser.mainFrame().get().executeJavaScript("document.title");
        if(value!=null && value.contains("-")){
            value=value.split("-")[0].trim();
        }
        if(value!=null){
            name=value;
        }
        return name;
    }

    public  int  getPageCount(){
        if(totalPage==0){
            String value=browser.mainFrame().get().executeJavaScript("$('.page-count').text()");
            if(value!=null){
                String pages=value.replace("/","");
                totalPage=Integer.parseInt(pages);
            }
        }
        return totalPage;
    }

    private void downloadPpt() throws InterruptedException, IOException {
        for(int p=1;p<=totalPage;p++){
            script="document.getElementsByClassName('reader-pageNo-"+p+"')[0].scrollIntoView();";
            SwingUtilities.invokeLater(()->{
                browser.mainFrame().get().executeJavaScript(script);
            });
            Thread.sleep(1000);

            script="$('.reader-pageNo-"+p+" div img').attr('src')";
            String value=browser.mainFrame().get().executeJavaScript(script);
            if(value!=null){
                BufferedImage pageImage= ImageIO.read(new URL(value));
                File pageFile=new File(tmpDir+File.separator+p+".png");
                ImageIO.write(pageImage, "PNG",pageFile);
            }
        }
    }
}
