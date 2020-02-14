package com.cvnavi.downloader;

import com.teamdev.jxbrowser.dom.Element;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import static com.cvnavi.downloader.ImageUtil.isLightGray;

public class LddocDownloader extends AbstractDownloader {
    String type;
    @Override
    public String getDocType() {
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:document:type']");
        ele.ifPresent(e->{
            type=e.attributeValue("content");
        });
        return type;
    }

    @Override
    public String getPageName() {
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:title']");
        ele.ifPresent(e->{
            name=e.attributeValue("content");
        });
        return name;
    }

    @Override
    public int getPageCount() {
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:document:page']");
        ele.ifPresent(e->{
            String s=e.attributeValue("content");
            if(s!=null &&s.length()>0){
                totalPage=Integer.parseInt(s);
            }
        });
        return totalPage;
    }

    @Override
    public void download() throws Exception {
        getPageCount();
        getPageName();
        getDocType();

        prepareDownload("lddoc.js");

        Thread.sleep(200);

        pageWidth=getJsFloat("jQuery('#outer_page_1').width()");
        pageHeight=getJsFloat("jQuery('#outer_page_1').height()");
        pageLeftMargin=getJsFloat("jQuery('#outer_page_1').offset().left");

        for(int p=1;p<=totalPage;p++){
            browser.mainFrame().get().executeJavaScript("document.getElementById(\"outer_page_"+p+"\").scrollIntoView();");
            Thread.sleep(3000);

            BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

            int segment=(int)Math.ceil(pageHeight/windowHeight);

            for(int i=0;i<segment;i++){
                final float scroll=i==0?0:windowHeight;;
                SwingUtilities.invokeLater(()->{
                    browser.mainFrame().get().executeJavaScript("window.scrollBy(0,"+scroll+")");
                });
                Thread.sleep(500);
                snapshot(pageImage,i);
            }
            removeWatermark(pageImage);
            writePageImage(pageImage,p);
        }

        writePdf();
    }

    private void removeWatermark(BufferedImage bi){
        for(int x = (int) (bi.getWidth()*0.3); x<bi.getWidth()*0.7; x++){
            for(int y = (int) (bi.getHeight()*0.4); y<bi.getHeight()*0.5; y++){

                if(isLightGray(bi.getRGB(x,y))
                        && isLightGray(bi.getRGB(x-1,y))
                        && isLightGray(bi.getRGB(x+1,y))
                        && isLightGray(bi.getRGB(x,y-1))
                        && isLightGray(bi.getRGB(x,y+1))){
                    bi.setRGB(x,y,0xFFFFFF);
                }
            }
        }
    }

}
