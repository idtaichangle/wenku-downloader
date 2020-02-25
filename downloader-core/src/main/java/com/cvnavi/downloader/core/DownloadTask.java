package com.cvnavi.downloader.core;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.Document;
import com.cvnavi.downloader.base.AbstractDownloader;
import com.cvnavi.downloader.base.DownloaderSelector;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadRecord;
import com.cvnavi.downloader.ocr.OcrTask;
import com.cvnavi.downloader.ocr.PdfOcrQueue;
import com.cvnavi.downloader.util.EncryptUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

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
    private DownloaderCallback callback;
    @Getter @Setter
    private Document result;

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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        AbstractDownloader downloader= DownloaderSelector.select(url);
        if(downloader!=null){
            try {
                log.debug("begin download "+url);
                clearTmpDir();
                Document.Meta meta=downloader.fetchMeta();
                if(callback!=null){
                    callback.metaReady(getId(),meta);
                }
                if(meta!=null){
                    result=downloader.download(meta);
                    DownloadRecord record= DownloadRecordDao.find(getId());
                    record.setName(result.getMeta().getName());
                    record.setEncryptName(EncryptUtil.md5(getUrl()));
                    DownloadRecordDao.update(record);

                    if(result.getFile()!=null){
                        OcrTask ocr=new OcrTask(this);
                        PdfOcrQueue.getInstance().submitTask(ocr);
                    }else{
                        invokeCallback(false,null);
                    }
                }else{
                    invokeCallback(false,null);
                }
                log.debug("finish download "+url);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                invokeCallback(false,null);
            }
        }
    }

    private void invokeCallback(boolean success,String fileName){
        if(callback!=null){
            callback.downloadFinish(getId(),success,fileName);
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
