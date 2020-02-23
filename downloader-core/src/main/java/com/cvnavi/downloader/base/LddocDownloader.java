package com.cvnavi.downloader.base;

import com.cvnavi.downloader.Holder;
import com.teamdev.jxbrowser.dom.Element;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static com.cvnavi.downloader.util.ImageUtil.isLightGray;

public class LddocDownloader extends AbstractDownloader {
    String type;

    public LddocDownloader(){
        prepareJsFile="lddoc.js";
    }

    @Override
    public String getDocType() {
        String value=executeJavaScript("$(\"meta[property='og:document:type']\").attr(\"content\")");
        return value;
    }

    @Override
    public String getPageName() {
        String value=executeJavaScript("$(\"meta[property='og:title']\").attr(\"content\")");
        if(value!=null && value.contains(".")){
            value=value.substring(0,value.lastIndexOf("."));
        }
        return value;
    }

    @Override
    public int getPageCount() {
        Holder<Integer> holder=new Holder<>(0);
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:document:page']");
        ele.ifPresent(e->{
            String s=e.attributeValue("content");
            if(s!=null &&s.length()>0){
                holder.set(Integer.parseInt(s));
            }
        });
        return holder.get();
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();
        pageWidth=getJsFloat("jQuery('#outer_page_1').width()");
        pageHeight=getJsFloat("jQuery('#outer_page_1').height()");
        pageLeftMargin=getJsFloat("jQuery('#outer_page_1').offset().left");
    }


    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

        executeJavaScriptAsync("document.getElementById(\"outer_page_"+p+"\").scrollIntoView();");
        Thread.sleep(3000);


        int segment=(int)Math.ceil(pageHeight/windowHeight);

        for(int i=0;i<segment;i++){
            float scroll=i==0?0:windowHeight;;
            executeJavaScriptAsync("window.scrollBy(0,"+scroll+")");
            Thread.sleep(100);
            snapshot(pageImage,i);
        }
        removeWatermark(pageImage);
        return pageImage;
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
