package com.cvnavi.downloader.common;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class DownloaderQueue extends LinkedBlockingQueue<DownloadTask>{

    boolean running=true;
    private DownloadTask STOP_QUEUE_FLAG=new DownloadTask();

    public DownloaderQueue(){
        startQueue();
    }

    public void startQueue(){
        new Thread(()->{
            try {
                while(running){
                    DownloadTask task=take();
                    if(task==STOP_QUEUE_FLAG){
                        break;
                    }else{
                        log.info("begin task. url="+task.getUrl());
                        task.download();
                    }
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public void stopQueue(){
        running=false;
        clear();
        offer(STOP_QUEUE_FLAG);
    }
}
