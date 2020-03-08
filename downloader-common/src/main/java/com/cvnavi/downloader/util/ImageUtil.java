package com.cvnavi.downloader.util;

public class ImageUtil {

    public static boolean isLightGray(int rgb){
        int r=(rgb>>16)&0xFF;
        int g=(rgb>>8)&0xFF;
        int b=(rgb)&0xFF;
        return (Math.abs(r-g)<=5) && (Math.abs(r-b)<=5) && r>=160;
    }

}
