package com.cvnavi.downloader.base;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cvnavi.downloader.util.ImageUtil.isLightGray;

/**
 * 下载 http://ishare.iask.sina.com.cn/文档
 */
public class IshareDownloader extends AbstractDownloader {

    public IshareDownloader(){
        prepareJsFile="js/ishare.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "ishare.iask.sina.com.cn"
        };
    }

    private HashSet<String> set=new HashSet<>();
    @Override
    protected void detectPageReady(String url) {
        Matcher m= Pattern.compile("\\?ssig=.+range=.+").matcher(url);
        if(m.find()){
            set.add(m.group(0));
            pageReady.add(set.size());
        }
    }

    @Override
    public BufferedImage downloadPage(int p) throws Exception {
        BufferedImage pageImage=super.downloadPage(p);
        removeWatermark(pageImage);
        return pageImage;
    }

    private void removeWatermark(BufferedImage bi){
        for(int x = (int) (bi.getWidth()*0.15); x<bi.getWidth()*0.85; x++){
            for(int y = (int) (bi.getHeight()*0.2); y<bi.getHeight()*0.8; y++){

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
