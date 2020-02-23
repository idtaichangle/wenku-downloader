package com.cvnavi.downloader.web;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.core.DownloadTask;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadRecord;
import com.cvnavi.downloader.web.ws.WebSocketServer;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Date;

@RestController
@Log4j2
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
        DownloadRecordDao.insert(record);

        DownloadTask task=new DownloadTask();
        task.setId(record.getId());
        task.setUrl(url);
        task.setCallback(WebSocketServer.callback);

        BrowserFrame.instance().submitDownloadTask(task);
        return new ModelAndView("preview","id",record.getId());
    }

    @RequestMapping(value = "/download")
    public void download(@RequestParam String fileName,
                         @RequestParam(defaultValue = "true") boolean download,
                           HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");

        DownloadRecord record=DownloadRecordDao.findByEncryptName(fileName);
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
}
