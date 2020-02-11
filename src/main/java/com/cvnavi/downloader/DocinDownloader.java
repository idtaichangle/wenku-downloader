package com.cvnavi.downloader;


import com.teamdev.jxbrowser.dom.Element;
import com.teamdev.jxbrowser.view.swing.BitmapUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * 下载 https://www.docin.com/文档
 */
public class DocinDownloader extends AbstractDownloader{

    String script="";

    @Override
    public boolean allowFlash() {
        return true;
    }

    @Override
    public void download() throws Exception {
        getPageCount();
        getPageName();
        getDocType();

        prepareDownload("docin.js");

        if(getDocType().contains(".ppt")){
            script="jQuery('#contentcontainer').css('padding-top',0);";
            browser.mainFrame().get().executeJavaScript(script);
            pageWidth=getJsFloat("jQuery('#page_1 .panel_inner').width();");
            pageHeight=getJsFloat("jQuery('#page_1 .panel_inner').height()");
            pageLeftMargin=getJsFloat("jQuery('#page_1 .panel_inner').offset().left");
        }else{
            pageWidth=getJsFloat("document.getElementById('page_1').clientWidth");
            pageHeight=getJsFloat("document.getElementById('page_1').clientHeight");
            pageLeftMargin=getJsFloat("jQuery('#page_1').offset().left");
        }

        Thread.sleep(500);

        for(int p=1;p<=totalPage;p++){
            BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

            script="docinReader.gotoPage("+p+",1);";
            SwingUtilities.invokeLater(()->{
                browser.mainFrame().get().executeJavaScript(script);
            });
            Thread.sleep(500);

            script="document.getElementById('page_"+p+"').scrollIntoView();";
            SwingUtilities.invokeLater(()->{
                browser.mainFrame().get().executeJavaScript(script);
            });
            Thread.sleep(2000);

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

    public String getDocType(){
        docType=".doc";
        script="jQuery('.info_list dd:nth-child(2)').text()";
        String value=browser.mainFrame().get().executeJavaScript(script);
        if(value!=null){
            docType=value;
        }
        return docType;
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
            String value=browser.mainFrame().get().executeJavaScript("jQuery('.page_num').text()");
            if(value!=null){
                String pages=value.replace("/","");
                if(pages.length()>0){
                    totalPage=Integer.parseInt(pages);
                }
            }
        }
        return totalPage;
    }


}
