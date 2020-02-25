package com.cvnavi.downloader.base;


public class DownloaderSelector {

    public static AbstractDownloader select(String url){
        AbstractDownloader downloader=null;

        if(url.contains("wenku.baidu.com")){
            downloader=new BaiduDownloader();
        }else if(url.contains("doc88.com")){
            downloader=new Doc88Downloader();
        }else if(url.contains("docin.com")){
            downloader=new DocinDownloader();
        }else if(url.contains("dangdang.com")){
            downloader=new DangDangDownloader();
        }else if(url.contains("lddoc.cn")){
            downloader=new LddocDownloader();
        }else if(url.contains("ishare.iask.sina.com.cn")){
            downloader=new IshareDownloader();
        }else if(url.contains("doc.mbalib.com")){
            downloader=new MbalibDownloader();
        }else if(url.contains("max.book118.com")){
            downloader=new Book118Downloader();
        }
        if(downloader!=null){
            downloader.setUrl(url);
        }
        return downloader;
    }
}
