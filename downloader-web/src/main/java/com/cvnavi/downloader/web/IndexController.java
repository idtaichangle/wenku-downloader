package com.cvnavi.downloader.web;

import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.common.DownloadTask;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadRecord;
import com.cvnavi.downloader.web.ws.WebSocketServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@RestController
public class IndexController extends BaseController{

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/preview",method = RequestMethod.POST)
    public Object preview(String url){
        if(url!=null){
            url=url.toLowerCase().trim();
            if(!url.startsWith("http://") && !url.startsWith("https://")){
                url="http://"+url;
            }
        }

        DownloadRecord record=new DownloadRecord();
        record.setUrl(url);
        record.setCreateTime(new Date().getTime());
        DownloadRecordDao.insertDownloadRecord(record);

        DownloadTask task=new DownloadTask();
        task.setId(record.getId());
        task.setUrl(url);
        task.setCallback(WebSocketServer.callback);

        BrowserFrame.instance().submitDownloadTask(task);
        return new ModelAndView("preview","id",record.getId());
    }

    @RequestMapping(value = "/download",method = RequestMethod.POST)
    public Object download(String url){
//        BrowserFrame.instance().download(null);
        return result(true);
    }
}
