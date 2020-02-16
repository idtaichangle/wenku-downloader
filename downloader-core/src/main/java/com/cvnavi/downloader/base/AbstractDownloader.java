package com.cvnavi.downloader.base;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.common.DownloaderCallback;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.util.ResourceReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.view.swing.BitmapUtil;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Log4j2
public abstract class AbstractDownloader {

    static String tmpDir= Config.TMP_DIR;

    int totalPage=0;
    String name;
    String docType;

    String prepareJsFile=null;

    float windowHeight;
    float pageWidth;
    float pageHeight;
    float pageLeftMargin;
    float screenScale;

    Browser browser= BrowserFrame.instance().getBrowser();

    private DownloaderCallback callback;

    public AbstractDownloader(DownloaderCallback callback){
        this.callback=callback;
    }

    public abstract String getPageName();

    public abstract int  getPageCount();

    public abstract BufferedImage downloadPage(int page) throws Exception;

    public  String getDocType(){
        return null;
    }

    public  void prepareDownload(){
        getPageCount();
        getPageName();
        getDocType();

        windowHeight=getJsFloat("window.innerHeight");//不包含水平滚动条

        String script=ResourceReader.read(prepareJsFile);
        if(script!=null && script.length()>0){
            executeJavaScript(script);
        }

        Component widget = (Component) BrowserFrame.instance().getBrowserView().getComponent(0);

        screenScale= BitmapUtil.toBufferedImage(browser.bitmap()).getWidth()/widget.getWidth();
    }

    public void download() throws Exception {
        prepareDownload();

        Thread.sleep(1000);

        for(int p=1;p<=totalPage;p++){
            BufferedImage pageImage=downloadPage(p);
            writePageImage(pageImage,p);
        }
        writePdf();
    }

    protected String executeJavaScript(String script){
        Object obj= browser.mainFrame().get().executeJavaScript(script);
        if(obj!=null){
            return obj.toString();
        }else{
            return null;
        }
    }

    public float getJsFloat(String script){
        Double value=browser.mainFrame().get().executeJavaScript(script);
        return value.floatValue();
    }

    protected void snapshot(BufferedImage pageImage,int segment){
        Graphics g= pageImage.createGraphics();
        BufferedImage shot= BitmapUtil.toBufferedImage(browser.bitmap());
        float height=shot.getHeight();
        shot=shot.getSubimage((int) (pageLeftMargin*screenScale),0, pageImage.getWidth(),shot.getHeight());

        int yOffset= (int) ((segment)*height);
        g.drawImage(shot,0,yOffset,null);
    }

    protected void writePageImage(BufferedImage pageImage,int pageIndex) throws IOException {
        File pageFile=new File(tmpDir+File.separator+pageIndex+".png");
        ImageIO.write(pageImage, "PNG",pageFile);
    }

    protected  void writePdf() throws IOException, DocumentException {

        if(new File(tmpDir+File.separator+"1.png").exists()){
            FileOutputStream fos = new FileOutputStream(Config.FILES_DIR+File.separator+name+".pdf");

            Image first=Image.getInstance(tmpDir+File.separator+"1.png");
            com.itextpdf.text.Rectangle r=first.getWidth()>first.getHeight()? PageSize.A4.rotate():PageSize.A4;
            Document document = new Document(r,0,0,0,0);
            PdfWriter writer = PdfWriter.getInstance(document, fos);

            writer.open();
            document.open();

            float documentWidth = document.getPageSize().getWidth();
            float documentHeight = document.getPageSize().getHeight();

            try{
                for(int i=1;i<=totalPage;i++){
                    Image image=Image.getInstance(tmpDir+File.separator+i+".png");
                    image.scaleAbsolute(documentWidth, documentHeight);
                    document.add(image);
                }
                if(callback!=null){
                    callback.downloadFinish(true);
                }
            }catch (Exception ex){
                log.error(ex.getMessage());
                if(callback!=null){
                    callback.downloadFinish(false);
                }
            }
            document.close();
            writer.close();

        }else{
            if(callback!=null){
                callback.downloadFinish(false);
            }
        }
    }
}
