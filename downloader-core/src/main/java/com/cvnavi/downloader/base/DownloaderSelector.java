package com.cvnavi.downloader.base;


public class DownloaderSelector {

    private static AbstractDownloader[] ALL= {
        new BaiduDownloader(),
        new Book118Downloader(),
        new Doc88Downloader(),
        new DocinDownloader(),
        new IshareDownloader(),
        new LddocDownloader(),
        new MbalibDownloader()
    };

    public static AbstractDownloader select(String url){
        AbstractDownloader downloader=null;
        for(AbstractDownloader d:ALL){
            if(d.accept(url)){
                try {
                    downloader=d.getClass().newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
                break;
            }
        }
        if(downloader!=null){
            downloader.setUrl(url);
        }
        return downloader;
    }


    public static boolean accept(String url){
        return select(url)!=null;
    }
}
