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
        prepareJsFile="js/baidu.js";
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
            return super.downloadPage(p);
        }
        return null;
    }
}
