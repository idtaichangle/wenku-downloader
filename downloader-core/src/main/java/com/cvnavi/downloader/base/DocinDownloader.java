package com.cvnavi.downloader.base;


import java.awt.image.BufferedImage;

import static com.cvnavi.downloader.util.ImageUtil.isLightGray;

/**
 * 下载 https://www.docin.com/文档
 */
public class DocinDownloader extends AbstractDownloader{

    public DocinDownloader(){
        prepareJsFile="docin.js";
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=super.downloadPage(p);
        removeWatermark(pageImage);
        return pageImage;
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
