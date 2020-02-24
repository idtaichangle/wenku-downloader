package com.cvnavi.downloader.base;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.Document;
import com.cvnavi.downloader.browser.BrowserFrame;
import com.cvnavi.downloader.util.EncryptUtil;
import com.cvnavi.downloader.util.ResourceReader;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.view.swing.BitmapUtil;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Log4j2
public abstract class AbstractDownloader {

    static String tmpDir= Config.TMP_DIR;
    protected String prepareJsFile=null;
    protected Document document=new Document();

    float windowHeight;
    float pageWidth;
    float pageHeight;
    float pageLeftMargin;
    float screenScale;

    protected Browser browser= BrowserFrame.instance().getBrowser();


    public abstract String getPageName();

    public abstract int  getPageCount();

    public abstract BufferedImage downloadPage(int page) throws Exception;

    public  String getDocType(){
        return null;
    }

    /**
     * 获取文档无数据。
     * @return
     */
    public Document.Meta fetchMeta(){
        Document.Meta meta=null;
        String name=getPageName();
        if(name!=null && name.length()>0){
            meta=new Document.Meta();
            meta.setName(name);
            meta.setTotalPage(getPageCount());
            meta.setType(getDocType());
        }
        return meta;
    }

    public  void prepareDownload(){

        windowHeight=getJsFloat("window.innerHeight");
        String script=new String(ResourceReader.readFile(prepareJsFile));
        if(script!=null && script.length()>0){
            executeJavaScript(script);
        }

        screenScale= BitmapUtil.toBufferedImage(browser.bitmap()).getWidth()/getJsFloat("window.innerWidth");
    }

    public Document download() throws Exception {
        Document.Meta meta = fetchMeta();
        return download(meta);
    }
    public Document download(Document.Meta meta) throws Exception {
        if(meta!=null){
            document.setMeta(meta);
            SwingUtilities.invokeLater(()->prepareDownload());
            Thread.sleep(1000);
            for(int p=1;p<=meta.getTotalPage();p++){
                BufferedImage pageImage=downloadPage(p);
                if(pageImage!=null){
                    writePageImage(pageImage,p);
                }else{
                    throw new RuntimeException("error download page "+p);
                }
            }
            writePdf();
        }
        return document;
    }

    protected String executeJavaScript(String script){
        Object obj= browser.mainFrame().get().executeJavaScript(script);
        if(obj!=null){
            return obj.toString();
        }else{
            return null;
        }
    }

    protected void executeJavaScriptAsync(final String script){
        SwingUtilities.invokeLater(()->{
            browser.mainFrame().get().executeJavaScript(script);
        });
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

            String name=document.getMeta().getName()+System.currentTimeMillis();
            name= EncryptUtil.md5(name);
            String outputFile=Config.FILES_DIR+File.separator+name+".pdf";
            FileOutputStream fos = new FileOutputStream(outputFile);

            Image first=Image.getInstance(tmpDir+File.separator+"1.png");
            com.itextpdf.text.Rectangle r=first.getWidth()>first.getHeight()? PageSize.A4.rotate():PageSize.A4;
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(r,0,0,0,0);
            PdfWriter writer = PdfWriter.getInstance(doc, fos);

            writer.open();
            doc.open();

            float documentWidth = doc.getPageSize().getWidth();
            float documentHeight = doc.getPageSize().getHeight();

            try{
                for(int i=1;i<=document.getMeta().getTotalPage();i++){
                    Image image=Image.getInstance(tmpDir+File.separator+i+".png");
                    image.scaleAbsolute(documentWidth, documentHeight);
                    doc.add(image);
                }
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
            doc.close();
            writer.close();
            document.setFile(outputFile);
        }
    }
}
