package com.cvnavi.downloader.browser;


import com.cvnavi.downloader.core.DownloadTask;
import com.cvnavi.downloader.core.DownloaderQueue;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.event.Observer;
import com.teamdev.jxbrowser.navigation.Navigation;
import com.teamdev.jxbrowser.navigation.event.FrameLoadFinished;
import com.teamdev.jxbrowser.navigation.event.LoadFinished;
import com.teamdev.jxbrowser.navigation.event.NavigationEvent;
import com.teamdev.jxbrowser.net.UrlRequest;
import com.teamdev.jxbrowser.net.event.RequestCompleted;
import com.teamdev.jxbrowser.view.swing.BrowserView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

    @Getter @Setter
    private Observer<RequestCompleted> requestCompletedObserver;


    static BrowserFrame browserFrame=new BrowserFrame();

    public static BrowserFrame instance(){
        return browserFrame;
    }

    private boolean firstShow=false;

    private BrowserFrame(){
        frame=new JFrame(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if(!firstShow){
                    firstShow=true;
                    SwingUtilities.invokeLater(()->{
                        onFrameShow();
                    });
                }
            }
        };
        browser=JxBrowserEngine.getEngine().newBrowser();
        JxBrowserEngine.getEngine().network().on(RequestCompleted.class, (event)->{
            if(requestCompletedObserver!=null){
                requestCompletedObserver.on(event);
            }
        });
        browserView=BrowserView.newInstance(browser);
        queue=new DownloaderQueue();
    }

    public void showFrame(){

        if(!frame.isShowing()){
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setSize(1440,900);
            frame.setVisible(true);
        }
    }

    private void onFrameShow(){
        browserView.setPreferredSize(new Dimension(frame.getWidth()-40,3000));
        JScrollPane scrollPane=new JScrollPane(browserView);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.revalidate();
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
//            nav.on(FrameLoadFinished.class, (event)->{
//                latch.countDown();
//            });
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

    public static void main(String[] args) {
        BrowserFrame.instance().showFrame();
        SwingUtilities.invokeLater(()->{
            BrowserFrame.instance().browse("http://www.baidu.com",null);
        });
    }
}
