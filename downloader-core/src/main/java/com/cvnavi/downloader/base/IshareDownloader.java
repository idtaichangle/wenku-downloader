package com.cvnavi.downloader.base;

import java.awt.image.BufferedImage;

public class IshareDownloader extends AbstractDownloader {
    public IshareDownloader(){
        prepareJsFile="ishare.js";
    }

    @Override
    public String getDocType() {
        String value=executeJavaScript("$('.crumb strong').text();");
        if(value!=null && value.contains(".")){
            value=value.substring(value.lastIndexOf(".")+1);
        }else{
            value="pdf";
        }
        return value;
    }

    @Override
    public String getPageName() {
        String value=executeJavaScript("$('.crumb strong').text();");
        if(value!=null && value.contains(".")){
            value=value.substring(0,value.lastIndexOf("."));
        }
        return value;
    }

    @Override
    public int getPageCount() {
        int totalPage=0;
        String value=browser.mainFrame().get().executeJavaScript("$('.page-input-con span').text();");
        if(value!=null && value.length()>0){
            totalPage=Integer.parseInt(value);
        }
        return totalPage;
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();
        pageWidth=getJsFloat("$(\"div[data-num='1']\").width()");
        pageHeight=getJsFloat("$(\"div[data-num='1']\").height()");
        pageLeftMargin=getJsFloat("$(\"div[data-num='1']\").offset().left");

    }


    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

        executeJavaScriptAsync("$(\"div[data-num='"+p+"']\")[0].scrollIntoView();");
        Thread.sleep(3000);


        int segment=(int)Math.ceil(pageHeight/windowHeight);

        for(int i=0;i<segment;i++){
            float scroll=i==0?0:windowHeight;;
            executeJavaScriptAsync("window.scrollBy(0,"+scroll+")");
            Thread.sleep(100);
            snapshot(pageImage,i);
        }
        return pageImage;
    }
}
