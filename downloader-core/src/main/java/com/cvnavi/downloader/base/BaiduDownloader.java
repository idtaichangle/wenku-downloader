package com.cvnavi.downloader.base;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下载baidu wenku的文档
 */
public class BaiduDownloader extends AbstractDownloader {

    public BaiduDownloader(){
        prepareJsFile="js/baidu.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "wenku.baidu.com"
        };
    }

    @Override
    protected void detectPageReady(String url) {
        Matcher m= Pattern.compile("(?<=&page=)\\d+(?=&)").matcher(url);
        if(m.find()){
            int page=Integer.parseInt(m.group(0));
            pageReady.add(page);
        }
    }

    protected void waitPageReady(int page) throws InterruptedException {
        for(int i=0;i<snapshotInterval/100;i++){
            if(pageReady.contains(page)){
                break;
            }
            Thread.sleep(100);
        }
        Thread.sleep(1000);
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        if(document.getMeta().getType().contains("ppt")){
            executeJavaScriptAsync("goToPage("+p+")");
            Thread.sleep(snapshotInterval);
            String script="$('.reader-pageNo-"+p+" div img').attr('src')";
            String value=executeJavaScript(script);
            if(value!=null){
                BufferedImage pageImage= ImageIO.read(new URL(value));
                return pageImage;
            }
        }else{
            BufferedImage pageImage=createImage();
            int p2=p%50;
            if(p2==0){
                p2=50;
            }
            executeJavaScriptAsync("goToPage("+p2+")");

            waitPageReady(p2);

            int segment=(int)Math.ceil(pageHeight/windowHeight);

            for(int i=0;i<segment;i++){
                Thread.sleep(500);
                snapshot(pageImage,i);
                scrollPage();
            }
            writePageImage(pageImage,p);

            if(p%50==0 && document.getMeta().getTotalPage()>50){
                executeJavaScriptAsync("$('#next-pageList-1').click()");
                Thread.sleep(5000);
                insertScript();
                prepareDownload();
            }
            return pageImage;
        }
        return null;
    }
}
