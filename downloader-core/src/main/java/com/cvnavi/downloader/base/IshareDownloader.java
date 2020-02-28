package com.cvnavi.downloader.base;

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
}
