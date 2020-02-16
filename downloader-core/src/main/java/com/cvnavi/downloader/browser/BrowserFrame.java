package com.cvnavi.downloader.browser;


import com.cvnavi.downloader.common.DownloadTask;
import com.cvnavi.downloader.common.DownloaderQueue;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.time.Timestamp;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;

@Log4j2
public class BrowserFrame {

    @Getter
    private Browser browser=null;
    @Getter
    private BrowserView browserView=null;
    private JFrame frame=null;
    private DownloaderQueue queue;

    private static Engine engine;

    static {
        JXBrowserCrack.crack();
        EngineOptions options=EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).build();
        engine=Engine.newInstance(options);
    }

    static BrowserFrame browserFrame=new BrowserFrame();

    public static BrowserFrame instance(){
        return browserFrame;
    }

    private BrowserFrame(){
        frame=new JFrame();
        browser=engine.newBrowser();
        browserView=BrowserView.newInstance(browser);
        queue=new DownloaderQueue(this);
    }

    public void showFrame(){

        if(!frame.isShowing()){
            frame.add(browserView, BorderLayout.CENTER);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setSize(1440,900);
            frame.setVisible(true);
            frame.addWindowStateListener((e)->{
                frame.remove(browserView);
                frame.add(browserView, BorderLayout.CENTER);
                frame.revalidate();
            });
        }
    }
    public void browse(String url){
        if(url!=null){
            browser.navigation().loadUrl(url);
        }
    }

    public void browseAndWait(String url){
        if(url!=null){
            browser.navigation().loadUrlAndWait(url, Timestamp.fromSeconds(30));
        }
    }

    public void submitDownloadTask(DownloadTask task){
        queue.offer(task);
    }

//    public BufferedImage downloadFirstPage(){
//        downloader=DownloaderSelector.select(url,null);
//        if(downloader!=null){
//            clearTmpDir();
//            downloader.prepareDownload();
//            try {
//                Thread.sleep(1000);
//                BufferedImage pageImage=downloader.downloadPage(1);
//                return pageImage;
//            } catch (Exception e) {
//            }
//        }
//        return null;
//    }

    public static void main(String[] args) {
        BrowserFrame.instance().showFrame();
        SwingUtilities.invokeLater(()->{
            BrowserFrame.instance().browse("http://www.baidu.com");
        });
    }
}
