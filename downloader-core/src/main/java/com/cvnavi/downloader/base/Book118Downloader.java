package com.cvnavi.downloader.base;

import com.teamdev.jxbrowser.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Book118Downloader extends AbstractDownloader {
    public Book118Downloader(){
        prepareJsFile="js/book118.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "max.book118.com"
        };
    }

    @Override
    public String getDocType() {
        String html = browser.mainFrame().get().html();
        html=html.substring(html.indexOf("filetype=")+9);
        html=html.substring(0,html.indexOf('\''));
        return html;
    }

    public String getPageName() {
        String value=executeJavaScript("doc_title");
        return value;
    }

    @Override
    public int getPageCount() {
        int totalPage=0;
        String value=executeJavaScript("$('#pagenumber').text();");
        if(value!=null && value.length()>0){
            totalPage=Integer.parseInt(value.trim().replace("页",""));
        }
        return totalPage;
    }

    @Override
    public void prepareDownload(){
        super.prepareDownload();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementById("p0");
        if(ele.isPresent()){

        }else{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            SwingUtilities.invokeLater(()->{
                String script="$('#newView .bar').remove();$('#alert').remove();";
                executeJavaScript(script);

                pageWidth=getJsFloat("$('div[data-id=1] img').width()");
                pageHeight=getJsFloat("$('div[data-id=1] img').height()");
                pageLeftMargin=getJsFloat("$('div[data-id=1] img').offset().left");
            });
        }
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        if(document.getMeta().getType().contains("ppt")){

        }else{
            //book118有两种页面样式
            Optional<Element> ele=browser.mainFrame().get().document().get().findElementById("p0");
            if(ele.isPresent()){
                String script="$('#p"+(p-1)+"')[0].scrollIntoView();";
                executeJavaScript(script);
                Thread.sleep(1000);
                script="$('#p"+(p-1)+" img').prop('src')";
                String url=executeJavaScript(script);
                if(url!=null && url.length()>0){
                    BufferedImage pageImage= ImageIO.read(new URL(url));
                    return pageImage;
                }
            }else{
                String script="$('div[data-id="+p+"]')[0].scrollIntoView();";
                executeJavaScript(script);
                Thread.sleep(1000);
                script="$('div[data-id="+p+"] img').prop('src')";
                String url=executeJavaScript(script);
                if(url!=null && url.length()>0){
                    BufferedImage pageImage= ImageIO.read(new URL(url));
                    return pageImage;
                }
            }
        }

        return null;
    }
}
