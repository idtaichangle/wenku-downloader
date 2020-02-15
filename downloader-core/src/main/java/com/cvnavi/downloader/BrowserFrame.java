package com.cvnavi.downloader;


import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BrowserFrame {

    JFrame frame;
    JTextField textField=new JTextField();
    JButton browserButton=new JButton("浏览(B)");
    JButton downloadButton =new JButton("下载(D)");
    JPanel content=null;
    Browser browser=null;
    BrowserView browserView=null;
    AbstractDownloader downloader=null;
    static Engine engine;
    private boolean showAddressBar=true;

    static {
        JXBrowserCrack.crack();
        EngineOptions options=EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).build();
        engine=Engine.newInstance(options);
    }

    public void showGUI(boolean showAddressBar){
        this.showAddressBar=showAddressBar;
        if(frame==null || !frame.isShowing()){
            SwingUtilities.invokeLater(()->{
                showFrame();
            });
        }
    }

    private void showFrame(){

        frame=new JFrame();
        content=new JPanel();
        content.setLayout(new BorderLayout());

        JPanel north=new JPanel();
        north.setLayout(new BorderLayout());
        north.add(textField,BorderLayout.CENTER);

        JPanel buttonPanel=new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        browserButton.setMnemonic('B');
        buttonPanel.add(browserButton,BorderLayout.WEST);
        downloadButton.setMnemonic('D');
        buttonPanel.add(downloadButton,BorderLayout.EAST);
        north.add(buttonPanel,BorderLayout.EAST);
        content.add(north,BorderLayout.NORTH);
        north.setVisible(showAddressBar);

        browser=engine.newBrowser();
        browserView=BrowserView.newInstance(browser);
        content.add(browserView,BorderLayout.CENTER);


        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    browse();
                }
            }
        });
        browserButton.addActionListener((evt)->{
            browse();
        });

        downloadButton.addActionListener((evt)->{
            download();
        });

        frame.setContentPane(content);
        frame.setSize(800,600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                System.exit(0);
            }
        });
        frame.setTitle("文档下载器");
        frame.setVisible(true);
    }

    public void browse(){
        if(textField.getText().length()>0){
            browse(textField.getText());
        }
    }

    public boolean browse(String url){
        if(url==null){
            return false;
        }

        browser.navigation().loadUrl(url);
        if(url.contains("wenku.baidu.com")){
            downloader=new BaiduDownloader();
        }else if(url.contains("doc88.com")){
            downloader=new Doc88Downloader();
        }else if(url.contains("docin.com")){
            downloader=new DocinDownloader();
        }else if(url.contains("dangdang.com")){
            downloader=new DangDangDownloader();
        }else if(url.contains("lddoc.cn")){
            downloader=new LddocDownloader();
        }else if(url.contains("ishare.iask.sina.com.cn")){
            downloader=new IshareDownloader();
        }else if(url.contains("doc.mbalib.com")){
            downloader=new MbalibDownloader();
        }else if(url.contains("max.book118.com")){
            downloader=new Book118Downloader();
        }

        if(downloader!=null){
            downloader.setBrowser(browser);
            downloader.setBrowserView(browserView);
            return true;
        }else{
            return false;
        }
    }

    public void download(){
        if(downloader!=null){
            for(int i=0;i<100;i++){
                try {
                    Files.deleteIfExists(Paths.get(System.getProperty("java.io.tmpdir")+File.separator+i+".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            new Thread(()->{
                try {
                    downloader.download();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }



    public static void main(String[] args) {
        new BrowserFrame().showGUI(true);
    }
}
