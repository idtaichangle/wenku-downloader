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
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Log4j2
public abstract class AbstractDownloader {

    @Setter
    private String url;
    static String tmpDir= Config.TMP_DIR;
    protected String prepareJsFile=null;
    protected Document document=new Document();

    float windowHeight;
    float pageWidth;
    float pageHeight;
    float pageLeftMargin;
    float screenScale;
    int snapshotInterval=1000;

    protected Browser browser= BrowserFrame.instance().getBrowser();

    public String[] acceptHost(){
        return new String[0];
    }

    public boolean accept(String url) {
        try {
            URL u=new URL(url);
            return Arrays.asList(acceptHost()).contains(u.getHost());
        } catch (MalformedURLException e) {
        }
        return false;
    }

    /**
     * 获取文档无数据。
     * @return
     */
    public Document.Meta fetchMeta(){
        insertScript();
        Document.Meta meta=null;
        String name=getDocName();
        if(name!=null && name.length()>0){
            meta=new Document.Meta();
            meta.setName(name);
            meta.setTotalPage(getPageCount());
            meta.setType(getDocType());
        }
        return meta;
    }

    private  boolean scriptInserted=false;
    private void insertScript(){
        if(!scriptInserted){
            String script=new String(ResourceReader.readFile(prepareJsFile));
            if(script!=null && script.length()>0){
                executeJavaScript(script);
            }
        }
        scriptInserted=true;
    }


    public  String getDocType(){
        return executeJavaScript("getDocType();");
    }

    public  String getDocName(){
        return executeJavaScript("getDocName();");
    }

    public int  getPageCount(){
        String page=executeJavaScript("getPageCount();");
        if(page!=null && page.length()>0){
            return Integer.parseInt(page);
        }
        return 0;
    }


    public  void prepareDownload() {
        windowHeight=getJsFloat("window.innerHeight");
        screenScale= BitmapUtil.toBufferedImage(browser.bitmap()).getWidth()/getJsFloat("window.innerWidth");

        executeJavaScriptAsync("prepare();");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        pageWidth=getJsFloat("getPageWidth();");
        pageHeight=getJsFloat("getPageHeight();");
        pageLeftMargin=getJsFloat("getPageLeftMargin();");
        snapshotInterval= (int) Float.parseFloat(executeJavaScript("snapshotInterval();"));
    }

    public Document download() throws Exception {
        Document.Meta meta = fetchMeta();
        return download(meta);
    }

    public Document download(Document.Meta meta) throws Exception {
        if(meta!=null){
            document.setMeta(meta);
            prepareDownload();
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

    public BufferedImage downloadPage(int page) throws Exception{
        BufferedImage pageImage=createImage();

        executeJavaScriptAsync("goToPage("+page+")");
        Thread.sleep(snapshotInterval);

        int segment=(int)Math.ceil(pageHeight/windowHeight);

        for(int i=0;i<segment;i++){
            float scroll=i==0?0:windowHeight;;
            executeJavaScriptAsync("window.scrollBy(0,"+scroll+")");
            Thread.sleep(200);
            snapshot(pageImage,i);
        }
        writePageImage(pageImage,page);
        return pageImage;
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

    protected BufferedImage createImage(){
        BufferedImage pageImage=new BufferedImage((int) (pageWidth*screenScale),(int)(pageHeight*screenScale),BufferedImage.TYPE_INT_RGB);
        return pageImage;
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
        if(Files.exists(Paths.get(tmpDir+File.separator+"1.png"))){

            String name= EncryptUtil.md5(url);;
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
            Files.copy(Paths.get(tmpDir+File.separator+"1.png"),
                    Paths.get(Config.FILES_DIR+File.separator+name+".png"));
        }
    }
}
