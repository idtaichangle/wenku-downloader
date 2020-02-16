package com.cvnavi.downloader.web;

import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.common.DownloadTask;
import com.cvnavi.downloader.web.ws.WebSocketServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController extends BaseController{

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    static int id=0;
    @RequestMapping(value = "/preview",method = RequestMethod.POST)
    public Object preview(String url){
        if(url!=null){
            url=url.toLowerCase().trim();
            if(!url.startsWith("http://") && !url.startsWith("https://")){
                url="http://"+url;
            }
        }
        id++;
        DownloadTask task=new DownloadTask();
        task.setId(id);
        task.setUrl(url);
        task.setCallback(WebSocketServer.callback);
        BrowserFrame.instance().submitDownloadTask(task);
        return new ModelAndView("preview","id",id);
    }

    @RequestMapping(value = "/download",method = RequestMethod.POST)
    public Object download(String url){
//        BrowserFrame.instance().download(null);
        return result(true);
    }
}
