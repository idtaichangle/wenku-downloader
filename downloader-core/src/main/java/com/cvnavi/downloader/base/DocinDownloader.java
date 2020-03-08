package com.cvnavi.downloader.base;


import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cvnavi.downloader.util.ImageUtil.isLightGray;

/**
 * 下载 https://www.docin.com/文档
 */
public class DocinDownloader extends AbstractDownloader{

    public DocinDownloader(){
        prepareJsFile="js/docin.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "www.docin.com"
        };
    }

    @Override
    protected void detectPageReady(String url) {
        Matcher m= Pattern.compile("(?<=_)\\d+(?=.docin)").matcher(url);
        if(m.find()){
            int page=Integer.parseInt(m.group(0))+1;// 第一页从0开始
            pageReady.add(page);
        }

        m= Pattern.compile("(?<=&pageno=)\\d+(?=&)").matcher(url);
        if(m.find()){
            int page=Integer.parseInt(m.group(0));
            pageReady.add(page);
        }
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=super.downloadPage(p);
        removeWatermark(pageImage);
        return pageImage;
    }

    private void removeWatermark(BufferedImage bi){
        for(int x = (int) (bi.getWidth()*0.05); x<bi.getWidth()*0.95; x++){
            for(int y = (int) (bi.getHeight()*0.35); y<bi.getHeight()*0.7; y++){

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
