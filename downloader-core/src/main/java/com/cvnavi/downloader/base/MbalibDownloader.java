package com.cvnavi.downloader.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    @Override
    protected void detectPageReady(String url) {
        Matcher m= Pattern.compile("(?<=num=)\\d+(?=&)").matcher(url);
        if(m.find()){
            int page=Integer.parseInt(m.group(0));
            pageReady.add(page);
        }
    }
}
