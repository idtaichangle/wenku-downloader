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

public class MainClass {

    JFrame frame;
    JTextField textField=new JTextField();
    JButton browserButton=new JButton("浏览(B)");
    JButton downloadButton =new JButton("下载(D)");
    JPanel content=null;
    Browser browser=null;
    BrowserView browserView=null;
    AbstractDownloader downloader=null;
    static Engine engine;

    static {
        JXBrowserCrack.crack();
        EngineOptions options=EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED).build();
        engine=Engine.newInstance(options);
    }

    public void showGUI(){
        final int viewWidth = 1024;
        final int viewHeight = 20000;

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
                System.exit(0);
            }
        });
        frame.setTitle("文档下载器");
        frame.setVisible(true);
    }

    public void browse(){
        if(browser==null){
            browser=engine.newBrowser();
            browserView=BrowserView.newInstance(browser);
            content.add(browserView,BorderLayout.CENTER);
            frame.getRootPane().updateUI();
        }
        if(textField.getText().length()>0){

            browser.navigation().loadUrl(textField.getText());

            if(textField.getText().contains("doc88.com")){
                downloader=new Doc88Downloader();
            }else if(textField.getText().contains("baidu.com")){
                downloader=new BaiduDownloader();
            }else if(textField.getText().contains("docin.com")){
                downloader=new DocinDownloader();
            }else if(textField.getText().contains("dangdang.com")){
                downloader=new DangDangDownloader();
            }else if(textField.getText().contains("lddoc.cn")){
                downloader=new LddocDownloader();
            }


            if(downloader!=null){
                downloader.setBrowser(browser);
                downloader.setBrowserView(browserView);
            }
        }
    }

    public void download(){
        if(downloader!=null){
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
        new MainClass().showGUI();
    }
}
