package com.cvnavi.downloader.base;

import com.teamdev.jxbrowser.dom.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Optional;

public class Book118Downloader extends AbstractDownloader {
    public Book118Downloader(){
        prepareJsFile="book118.js";
    }

    @Override
    public String getDocType() {
        return null;
    }

    @Override
    public String getPageName() {
        String value=browser.mainFrame().get().executeJavaScript("doc_title");
        if(value!=null){
            name=value;
        }
        return name;
    }

    @Override
    public int getPageCount() {
        String value=browser.mainFrame().get().executeJavaScript("$('#pagenumber').text();");
        if(value!=null && value.length()>0){
            totalPage=Integer.parseInt(value.trim().replace("页",""));
        }
        return totalPage;
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();
    }

    @Override
    public void download() throws Exception {

        prepareDownload();

        //book118有两种页面样式
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementById("p0");
        if(ele.isPresent()){

            for(int p=1;p<=totalPage;p++){
                executeJavaScript("$('#p"+(p-1)+"')[0].scrollIntoView();");
                String script="$('#p"+(p-1)+" img').prop('src')";
                String url=executeJavaScript(script);
                if(url!=null && url.length()>0){
                    BufferedImage pageImage= ImageIO.read(new URL(url));
                    writePageImage(pageImage,p);
                }
                Thread.sleep(1000);
            }

        }else{
            Thread.sleep(8000);
            String script="$('#btn_read').click()";
            executeJavaScript(script);
            Thread.sleep(3000);

            for(int p=1;p<=totalPage;p++){
                executeJavaScript("$('div[data-id="+p+"]')[0].scrollIntoView();");
                Thread.sleep(1000);
                script="$('div[data-id="+p+"] img').prop('src')";

                String url=executeJavaScript(script);

                if(url!=null && url.length()>0){
                    BufferedImage pageImage= ImageIO.read(new URL(url));
                    writePageImage(pageImage,p);
                }

            }
        }

        Thread.sleep(10000);
        writePdf();
    }

    @Override
    public BufferedImage downloadPage(int page) throws Exception {
        return null;
    }
}
