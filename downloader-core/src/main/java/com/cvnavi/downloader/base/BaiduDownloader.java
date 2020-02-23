package com.cvnavi.downloader.base;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * 下载baidu wenku的文档
 */
public class BaiduDownloader extends AbstractDownloader {

    public BaiduDownloader(){
        prepareJsFile="baidu.js";
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();
        if(! document.getMeta().getType().contains("ppt")){
            pageWidth=getJsFloat("$('.reader-page-1').width()");
            pageHeight=getJsFloat("$('.reader-page-1').height()");
            pageLeftMargin=getJsFloat("$('.reader-page-1').offset().left");
        }
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        if(document.getMeta().getType().contains("ppt")){
            String script="document.getElementsByClassName('reader-pageNo-"+p+"')[0].scrollIntoView();";
            executeJavaScriptAsync(script);
            Thread.sleep(1000);
            script="$('.reader-pageNo-"+p+" div img').attr('src')";
            String value=executeJavaScript(script);
            if(value!=null){
                BufferedImage pageImage= ImageIO.read(new URL(value));
                return pageImage;
            }
        }else{
            BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);
            String script="document.getElementsByClassName('reader-page-"+p+"')[0].scrollIntoView();";
            executeJavaScriptAsync(script);

            Thread.sleep(2000);

            int segment=(int)Math.ceil(pageHeight/windowHeight);
            for(int i=0;i<segment;i++){
                int scroll= (i==0?0: (int) windowHeight);
                executeJavaScriptAsync("window.scrollBy(0,"+scroll+")");
                Thread.sleep(100);
                snapshot(pageImage,i);
            }
            return pageImage;
        }
        return null;
    }


    public String getDocType(){
        String docType=null;
        String value=executeJavaScript(" window.__fisData._data.WkInfo.DocInfo.docType");
        if(value!=null){
            docType= value;
        }
        return docType;
    }

    public  String getPageName(){
        String name=null;
        String  value=executeJavaScript("document.title");
        if(value!=null && value.contains("-")){
            value=value.split("-")[0].trim();
        }
        if(value!=null){
            name=value;
        }
        return name;
    }

    public  int  getPageCount(){
        int totalPage=0;
        String value=executeJavaScript("$('.page-count').text()");
        if(value!=null){
            String pages=value.replace("/","");
            if(pages.length()>0){
                totalPage=Integer.parseInt(pages);
            }
        }
        return totalPage;
    }
}
