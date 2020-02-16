package com.cvnavi.downloader.common;

import com.cvnavi.downloader.browser.BrowserFrame;

import java.util.concurrent.LinkedBlockingQueue;

public class DownloaderQueue extends LinkedBlockingQueue<DownloadTask>{

    private BrowserFrame browserFrame;
    boolean running=true;
    private DownloadTask STOP_QUEUE_FLAG=new DownloadTask();

    public DownloaderQueue(BrowserFrame browserFrame) {
        this.browserFrame = browserFrame;
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
                        browserFrame.browseAndWait(task.getUrl());
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
