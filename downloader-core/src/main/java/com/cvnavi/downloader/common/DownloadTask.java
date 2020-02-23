package com.cvnavi.downloader.common;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.Document;
import com.cvnavi.downloader.base.AbstractDownloader;
import com.cvnavi.downloader.base.DownloaderSelector;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class DownloadTask{

    @Getter @Setter
    private int id;
    @Getter @Setter
    private String url;
    @Getter @Setter
    private File result;
    @Getter @Setter
    private DownloaderCallback callback;

    private boolean downloading=false;

    private Thread thread= new Thread(() -> doDownload());

    public void download(){
        downloading=false;
        BrowserFrame.instance().browse(getUrl(),(event)->{
            if(!downloading){//防止多次出发LoadFinished事件。
                downloading=true;
                thread.start();
            }
        });
        try {
            thread.join(60*1000);
        } catch (InterruptedException e) {
        }
    }

    private void doDownload(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        AbstractDownloader downloader= DownloaderSelector.select(url);
        if(downloader!=null){
            try {
                log.debug("begin download "+url);
                clearTmpDir();
                Document.Meta meta=downloader.fetchMeta();
                if(callback!=null){
                    callback.metaReady(this,meta);
                }
                if(meta!=null){
                    Document document=downloader.download(meta);
                    document.getFile();
                    DownloadRecord record= DownloadRecordDao.find(getId());
                    record.setName(document.getMeta().getName());
                    if(document.getFile()!=null){
                        String eName= Paths.get(document.getFile()).getFileName().
                                toString().replace(".pdf","");
                        record.setEncryptName(eName);
                    }
                    DownloadRecordDao.update(record);
                    if(callback!=null){
                        callback.downloadFinish(this,true,record.getEncryptName());
                    }
                }else{
                    if(callback!=null){
                        callback.downloadFinish(this,false,null);
                    }
                }
                log.debug("finish download "+url);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                if(callback!=null){
                    callback.downloadFinish(this,false,null);
                }
            }
        }
    }


    private void clearTmpDir()  {
        try {
            Files.list(Paths.get(Config.TMP_DIR)).forEach((f)->{
                try {
                    Files.delete(f);
                } catch (IOException e) {
                }
            });
        } catch (IOException e) {
        }
    }
}
