package com.cvnavi.downloader.base;

/**
 * 下载https://max.book118.com/的文档
 */
public class Book118Downloader extends AbstractDownloader {
    public Book118Downloader(){
        prepareJsFile="js/book118.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "max.book118.com"
        };
    }

    @Override
    public void prepareDownload() {
        executeJavaScriptAsync("openIframe();");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        insertScript();

        super.prepareDownload();
    }

}
