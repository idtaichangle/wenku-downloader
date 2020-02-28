package com.cvnavi.downloader.base;

import java.awt.image.BufferedImage;

import static com.cvnavi.downloader.util.ImageUtil.isLightGray;


/**
 * 下载 http://www.lddoc.cn/文档
 */
public class LddocDownloader extends AbstractDownloader {

    public LddocDownloader(){
        prepareJsFile="js/lddoc.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "www.lddoc.cn"
        };
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=super.downloadPage(p);
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
