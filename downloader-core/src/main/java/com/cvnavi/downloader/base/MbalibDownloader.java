package com.cvnavi.downloader.base;

/**
 * 下载 https://doc.mbalib.com/文档
 */
public class MbalibDownloader extends AbstractDownloader {

    public MbalibDownloader(){
        prepareJsFile="js/mbalib.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "doc.mbalib.com"
        };
    }
}
