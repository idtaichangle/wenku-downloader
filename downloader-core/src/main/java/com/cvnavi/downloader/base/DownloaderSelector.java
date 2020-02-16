package com.cvnavi.downloader.base;

import com.cvnavi.downloader.common.DownloaderCallback;

public class DownloaderSelector {

    public static AbstractDownloader select(String url, DownloaderCallback callback){
        AbstractDownloader downloader=null;

        if(url.contains("wenku.baidu.com")){
            downloader=new BaiduDownloader(callback);
        }else if(url.contains("doc88.com")){
            downloader=new Doc88Downloader(callback);
        }else if(url.contains("docin.com")){
            downloader=new DocinDownloader(callback);
        }else if(url.contains("dangdang.com")){
            downloader=new DangDangDownloader(callback);
        }else if(url.contains("lddoc.cn")){
            downloader=new LddocDownloader(callback);
        }else if(url.contains("ishare.iask.sina.com.cn")){
            downloader=new IshareDownloader(callback);
        }else if(url.contains("doc.mbalib.com")){
            downloader=new MbalibDownloader(callback);
        }else if(url.contains("max.book118.com")){
            downloader=new Book118Downloader(callback);
        }
        return downloader;
    }
}
