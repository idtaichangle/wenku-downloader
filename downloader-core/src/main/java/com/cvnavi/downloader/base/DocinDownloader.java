package com.cvnavi.downloader.base;


import com.cvnavi.downloader.Holder;
import com.teamdev.jxbrowser.dom.Element;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static com.cvnavi.downloader.util.ImageUtil.isLightGray;

/**
 * 下载 https://www.docin.com/文档
 */
public class DocinDownloader extends AbstractDownloader{

    public DocinDownloader(){
        prepareJsFile="docin.js";
    }

    @Override
    public void prepareDownload() {
        super.prepareDownload();
        if(getDocType().contains(".ppt")){
            String script="jQuery('#contentcontainer').css('padding-top',0);";
            executeJavaScript(script);
            pageWidth=getJsFloat("jQuery('#page_1 .panel_inner').width();");
            pageHeight=getJsFloat("jQuery('#page_1 .panel_inner').height()");
            pageLeftMargin=getJsFloat("jQuery('#page_1 .panel_inner').offset().left");
        }else{
            pageWidth=getJsFloat("document.getElementById('page_1').clientWidth");
            pageHeight=getJsFloat("document.getElementById('page_1').clientHeight");
            pageLeftMargin=getJsFloat("jQuery('#page_1').offset().left");
        }
    }


    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);

        String script="docinReader.gotoPage("+p+",1);";
        executeJavaScriptAsync(script);
        Thread.sleep(500);

        script="document.getElementById('page_"+p+"').scrollIntoView();";
        executeJavaScriptAsync(script);
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

    public String getDocType(){
        String script="jQuery('.info_list dd:nth-child(2)').text()";
        String value=executeJavaScript(script);
        return value;
    }

    public  String getPageName(){
        Holder<String> holder=new Holder<>();
        Optional<Element> ele=browser.mainFrame().get().document().get().findElementByCssSelector("meta[property='og:title']");
        ele.ifPresent(e->{
            holder.set(e.attributeValue("content"));
        });
        return holder.get();
    }

    public  int  getPageCount(){
        int totalPage=0;
        String value=executeJavaScript("jQuery('.page_num').text()");
        if(value!=null){
            String pages=value.replace("/","");
            if(pages.length()>0){
                totalPage=Integer.parseInt(pages);
            }
        }
        return totalPage;
    }

    private void removeWatermark(BufferedImage bi){
        for(int x = (int) (bi.getWidth()*0.1); x<bi.getWidth()*0.9; x++){
            for(int y = (int) (bi.getHeight()*0.35); y<bi.getHeight()*0.65; y++){

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
