package com.cvnavi.downloader.browser;


import com.cvnavi.downloader.core.DownloadTask;
import com.cvnavi.downloader.core.DownloaderQueue;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.event.Observer;
import com.teamdev.jxbrowser.navigation.Navigation;
import com.teamdev.jxbrowser.navigation.event.LoadFinished;
import com.teamdev.jxbrowser.navigation.event.NavigationEvent;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Log4j2
public class BrowserFrame {

    @Getter
    private Browser browser=null;
    @Getter
    private BrowserView browserView=null;
    private JFrame frame=null;
    private DownloaderQueue queue;

    static BrowserFrame browserFrame=new BrowserFrame();

    public static BrowserFrame instance(){
        return browserFrame;
    }

    private BrowserFrame(){
        frame=new JFrame();
        browser=JxBrowserEngine.getEngine().newBrowser();
        browserView=BrowserView.newInstance(browser);
        queue=new DownloaderQueue();
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

    public synchronized void browse(String url){
        browse(url,null);
    }

    public synchronized void browse(String url,Observer<NavigationEvent> observer){
        if(url!=null){
            Navigation nav= browser.navigation();
            CountDownLatch latch = new CountDownLatch(1);
            nav.on(LoadFinished.class, (event)->{
                latch.countDown();
            });
            nav.loadUrl(url);

            if(observer!=null){
                try {
                    latch.await(30L, TimeUnit.SECONDS);
                } catch (InterruptedException var19) {
                }
                observer.on(null);
            }
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
            BrowserFrame.instance().browse("http://www.baidu.com",null);
        });
    }
}
