package com.cvnavi.downloader.util;

public class ImageUtil {

    public static boolean isLightGray(int rgb){
        int r=(rgb>>16)&0xFF;
        int g=(rgb>>8)&0xFF;
        int b=(rgb)&0xFF;
        return r==g && r==b && r>=200;
    }

}
