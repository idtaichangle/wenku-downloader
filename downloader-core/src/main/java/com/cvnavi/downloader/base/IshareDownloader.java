package com.cvnavi.downloader.base;

import java.awt.image.BufferedImage;

public class IshareDownloader extends AbstractDownloader {
    public IshareDownloader(){
        prepareJsFile="ishare.js";
    }

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
            if(value!=null && value.length()>0){
                totalPage=Integer.parseInt(value);
            }
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

        executeJavaScript("$(\"div[data-num='"+p+"']\")[0].scrollIntoView();");
        Thread.sleep(3000);


        int segment=(int)Math.ceil(pageHeight/windowHeight);

        for(int i=0;i<segment;i++){
            float scroll=i==0?0:windowHeight;;
            executeJavaScript("window.scrollBy(0,"+scroll+")");
            Thread.sleep(100);
            snapshot(pageImage,i);
        }
        return pageImage;
    }
}
