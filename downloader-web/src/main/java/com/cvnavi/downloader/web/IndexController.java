package com.cvnavi.downloader.web;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.base.DownloaderSelector;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.core.DownloadTask;
import com.cvnavi.downloader.db.dao.DownloadFileDao;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadFile;
import com.cvnavi.downloader.db.model.DownloadRecord;
import com.cvnavi.downloader.db.model.User;
import com.cvnavi.downloader.util.ResourceReader;
import com.cvnavi.downloader.web.ws.WebSocketServer;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    public ModelAndView preview(String url, HttpSession session){

        HashMap<String,Object> model=new HashMap<>();
        Object user=session.getAttribute("user");
        model.put("login",(user!=null)?"true":"false");

        if(url!=null){
            url=url.trim();
            if(!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")){
                url="http://"+url;
            }
            if(DownloaderSelector.accept(url)){
                DownloadFile df=DownloadFileDao.findByUrl(url);
                DownloadRecord record=new DownloadRecord();
                if(user!=null){
                    record.setUserId(((User)user).getId());
                }
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

    @GetMapping(value="/previewImg", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getPreviewImg(int taskId){

        DownloadRecord dr=DownloadRecordDao.find(taskId);
        if(dr!=null && dr.getFileId()>0){
            DownloadFile df=DownloadFileDao.find(dr.getFileId());
            if(df!=null && df.getEncryptName()!=null){
                String file=Config.FILES_DIR+File.separator+df.getEncryptName()+".png";
                try {
                    return Files.readAllBytes(Paths.get(file));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return ResourceReader.readFile("static/img/not_found.png");
    }

    @RequestMapping("/queryTaskStatus")
    public Object queryTaskStatus(int taskId, HttpSession session){
        DownloadRecord record=DownloadRecordDao.find(taskId);
        if(record!=null){
            DownloadFile df=DownloadFileDao.findByUrl(record.getUrl());
            if(df!=null && Files.exists(Paths.get(Config.FILES_DIR+File.separator+df.getEncryptName()+".pdf"))){
                HashMap<String,Object> data=new HashMap<>();
                data.put("name",df.getName());
                data.put("fileName",df.getEncryptName());
                boolean paid=false;
                User user=(User) session.getAttribute("user");
                if(user!=null){
                    Collection<DownloadRecord> list=DownloadRecordDao.findByUrl(record.getUrl(),user.getId());
                    for(DownloadRecord r:list){
                        if(r.getPaymentTime()>0){
                            paid=true;
                        }
                    }
                }
                data.put("paid",paid);
                if(paid){
                    data.put("link",df.getEncryptName());
                }
                return result(true,"文件已经下载",data);
            }
        }
        return result(false,"文件尚未下载");
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
}
