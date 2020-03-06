package com.cvnavi.downloader.base;


import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下载http://www.doc88.com的文档
 */
public class Doc88Downloader extends AbstractDownloader{

    public Doc88Downloader() {
        prepareJsFile="js/doc88.js";
    }

    public String[] acceptHost(){
        return new String[]{
                "www.doc88.com"
        };
    }

    private HashSet<String> set=new HashSet<>();
    @Override
    protected void detectPageReady(String url) {
        Matcher m= Pattern.compile("getebt-.+.ebt$").matcher(url);
        if(m.find()){
            set.add(m.group(0));
            pageReady.add(set.size());
        }
    }
}