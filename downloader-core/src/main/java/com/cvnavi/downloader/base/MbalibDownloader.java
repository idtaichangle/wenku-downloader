package com.cvnavi.downloader.base;

import java.awt.image.BufferedImage;

public class MbalibDownloader extends AbstractDownloader {

    public MbalibDownloader(){
        prepareJsFile="mbalib.js";
    }

    @Override
    public String getDocType() {
        String value=executeJavaScript("wgDocType");
        return value;
    }

    @Override
    public String getPageName() {
        String value=executeJavaScript("wgDocTitle");
        return value;
    }

    @Override
    public int getPageCount() {
        int totalPage=0;
        String value=executeJavaScript("$(\".num span\").text();");
        if(value!=null && value.length()>0){
            totalPage=Integer.parseInt(value.replace("/",""));
        }
        return totalPage;
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();
        pageWidth=getJsFloat("$(\"#page0\").width()");
        pageHeight=getJsFloat("$(\"#page0\").height()");
        pageLeftMargin=getJsFloat("$(\"#page0\").offset().left");
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

        executeJavaScript("$(\"#page"+(p-1)+"\")[0].scrollIntoView()");
        Thread.sleep(3000);

        int segment=(int)Math.ceil(pageHeight/windowHeight);

        for(int i=0;i<segment;i++){
            float scroll=i==0?0:windowHeight;;
            executeJavaScript("window.scrollBy(0,"+scroll+")");
            Thread.sleep(100);
            snapshot(pageImage,i);
        }
        writePageImage(pageImage,p);
        return pageImage;
    }
}
