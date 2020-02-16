package com.cvnavi.downloader.base;



import com.cvnavi.downloader.common.DownloaderCallback;
import com.teamdev.jxbrowser.dom.Element;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Optional;

/**
 * 下载http://www.doc88.com的文档
 */
public class Doc88Downloader extends AbstractDownloader{

    public Doc88Downloader(DownloaderCallback callback) {
        super(callback);
        prepareJsFile="doc88.js";
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();

        pageWidth=getJsFloat("jQuery('#outer_page_1').width()");
        pageHeight=getJsFloat("jQuery('#outer_page_1').height()");
        pageLeftMargin=getJsFloat("jQuery('#outer_page_1').offset().left");
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {

        BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

        executeJavaScript("document.getElementById(\"outer_page_"+p+"\").scrollIntoView();");
        Thread.sleep(3000);

        int segment=(int)Math.ceil(pageHeight/windowHeight);

        for(int i=0;i<segment;i++){
            final float scroll=i==0?0:windowHeight;;
            SwingUtilities.invokeLater(()->{
                browser.mainFrame().get().executeJavaScript("window.scrollBy(0,"+scroll+")");
            });
            Thread.sleep(500);
            snapshot(pageImage,i);
        }
        return pageImage;
    }

    public String getDocType(){

        return null;
    }

    public  String getPageName(){
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:title']");
        ele.ifPresent(e->{
            name=e.attributeValue("content");
        });
        return name;
    }

    public  int  getPageCount(){
        if(totalPage==0){
            Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:document:page']");
            ele.ifPresent(e->{
                totalPage=Integer.parseInt( e.attributeValue("content"));
            });
        }
        return totalPage;
    }


    protected  void writeImage(int index,String imageString) {

        byte[] imageByte;
        try {
            Base64.Decoder decoder=Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            FileOutputStream fos=new FileOutputStream(tmpDir+ File.separator+index+".png");
            fos.write(imageByte);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}