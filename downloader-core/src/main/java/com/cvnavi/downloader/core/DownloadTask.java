package com.cvnavi.downloader.core;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.Document;
import com.cvnavi.downloader.base.AbstractDownloader;
import com.cvnavi.downloader.base.DownloaderSelector;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.db.dao.DownloadFileDao;
import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadFile;
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
    private AbstractDownloader downloader;
    @Getter @Setter
    private DownloaderCallback callback;
    @Getter @Setter
    private Document result;

    private boolean downloading=false;

    private Thread thread= new Thread(() -> doDownload());

    public void download(){
        DownloadFile record= DownloadFileDao.findByUrl(getUrl());
        if(record!=null && Files.exists(Paths.get(Config.FILES_DIR+ File.separator+record.getEncryptName()+".pdf"))){
            invokeCallback(true,record.getEncryptName());
        }else{
            downloading=false;
            downloader= DownloaderSelector.select(url);
            downloader.setTask(this);
            BrowserFrame.instance().setRequestCompletedObserver(downloader.getRequestCompletedObserver());

            BrowserFrame.instance().browse(getUrl(),(event)->{
                if(!downloading){//防止多次出发LoadFinished事件。
                    downloading=true;
                    thread.start();
                }
            });
            try {
                thread.join(600*1000);
            } catch (InterruptedException e) {
            }
        }
    }

    private void doDownload(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
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

                    if(result.getFile()!=null){
                        DownloadFile df=updateDatabase(result);

                        if(Config.PDF_OCR){
                            OcrTask ocr=new OcrTask(this);
                            PdfOcrQueue.getInstance().submitTask(ocr);
                        }else{
                            invokeCallback(true,df.getEncryptName());
                        }
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

    private DownloadFile updateDatabase(Document doc) {
        DownloadFile df=new DownloadFile();
        df.setUrl(getUrl());
        df.setCreateTime(System.currentTimeMillis());
        df.setName(result.getMeta().getName());
        df.setEncryptName(EncryptUtil.md5(getUrl()));
        df.setType(doc.getMeta().getType());
        df.setTotalPages(doc.getMeta().getTotalPage());
        DownloadFileDao.insert(df);

        DownloadRecord record= DownloadRecordDao.find(getId());
        record.setFileId(df.getId());
        DownloadRecordDao.update(record);
        return df;
    }

    private void invokeCallback(boolean success,String fileName){
        if(callback!=null){
            callback.documentReady(getId(),success,fileName);
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
