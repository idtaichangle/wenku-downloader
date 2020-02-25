package com.cvnavi.downloader.ocr;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public class PdfOcrQueue extends LinkedBlockingQueue<OcrTask> {

    private static PdfOcrQueue instance=new PdfOcrQueue();

    public static PdfOcrQueue getInstance(){
        return instance;
    }

    boolean running=true;
    private OcrTask STOP_QUEUE_FLAG=new OcrTask(null);

    private PdfOcrQueue(){
        startQueue();
    }

    public void startQueue(){
        new Thread(()->{
            try {
                while(running){
                    OcrTask task=take();
                    if(task==STOP_QUEUE_FLAG){
                        break;
                    }else{
                        task.doPdfOcr();
                    }
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public void submitTask(OcrTask ocr) {
        offer(ocr);
    }

    public void stopQueue(){
        running=false;
        clear();
        offer(STOP_QUEUE_FLAG);
    }
}
