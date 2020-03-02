package com.cvnavi.downloader.web;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.base.DownloaderSelector;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.core.DownloadTask;
import com.cvnavi.downloader.db.dao.DownloadFileDao;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadFile;
import com.cvnavi.downloader.db.model.DownloadRecord;
import com.cvnavi.downloader.web.ws.WebSocketServer;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

@RestController
@Log4j2
public class IndexController extends BaseController{

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/preview",method = RequestMethod.GET)
    public ModelAndView preview(){
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/preview",method = RequestMethod.POST)
    public ModelAndView preview(String url){

        HashMap<String,Object> model=new HashMap<>();

        if(url!=null){
            url=url.toLowerCase().trim();
            if(!url.startsWith("http://") && !url.startsWith("https://")){
                url="http://"+url;
            }
            if(DownloaderSelector.accept(url)){
                DownloadFile df=DownloadFileDao.findByUrl(url);
                DownloadRecord record=new DownloadRecord();
                record.setUrl(url);
                record.setCreateTime(System.currentTimeMillis());
                if(df!=null){
                    record.setFileId(df.getId());
                }
                DownloadRecordDao.insert(record);


                DownloadTask task=new DownloadTask();
                task.setId(record.getId());
                task.setUrl(url);
                task.setCallback(WebSocketServer.callback);
                BrowserFrame.instance().submitDownloadTask(task);
                model.put("taskCreated",true);
                model.put("id",record.getId());
                return new ModelAndView("preview",model);
            }
        }

        model.put("taskCreated",false);
        model.put("id",-1);
        return new ModelAndView("preview",model);
    }

    @RequestMapping("/queryTaskFile")
    public Object queryTaskFile(int taskId){
        DownloadRecord record=DownloadRecordDao.find(taskId);
        if(record!=null){
            DownloadFile df=DownloadFileDao.findByUrl(record.getUrl());
            if(df!=null){
                HashMap<String,Object> data=new HashMap<>();
                data.put("name",df.getName());
                data.put("fileName",df.getEncryptName());
                return result(true,"文件已经下载",data);
            }
        }
        return result(false,"文件已经尚未下载");
    }

    @RequestMapping(value = "/download")
    public void download(@RequestParam String fileName,
                         @RequestParam(defaultValue = "true") boolean download,
                           HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");

        DownloadFile record= DownloadFileDao.findByEncryptName(fileName);
        File f=null;
        if(record!=null){
            f=new File(Config.FILES_DIR+File.separator +record.getEncryptName()+".pdf");
        }
        if(record==null ||  f==null || !f.exists()){
            try {
                response.setHeader("Content-type", "application/json;charset=UTF-8");
                response.getWriter().println("{\"success\":false,\"message\":\"找不到此文件。\"}");
                return;
            } catch (IOException e) {
            }
        }
        InputStream in = null;
        try {
            in = new FileInputStream(f);
            if(download){
                response.setHeader("Content-disposition"," attachment; filename="+ URLEncoder.encode(record.getName()+".pdf","UTF-8"));
            }
            response.setContentType(Files.probeContentType(f.toPath()));
            IOUtils.copy(in, response.getOutputStream());

        } catch (FileNotFoundException e) {
            log.error("下载出错。" + fileName + "," + e);
        } catch (IOException e) {
            log.error("下载出错。" + fileName + "," + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/downloadRecord")
    public ModelAndView downloadRecord(@RequestParam(defaultValue = "1") int pageIndex,
                               @RequestParam(defaultValue = "10") int pageSize){
        Collection<DownloadRecord> list= DownloadRecordDao.list(pageIndex,pageSize);
        return new ModelAndView("download_record","list",list);
    }


    @RequestMapping("/test")
    public ModelAndView test(){
        return new ModelAndView("test");
    }

}
