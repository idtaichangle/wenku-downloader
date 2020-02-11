package com.cvnavi.downloader;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.js.JsObject;
import com.teamdev.jxbrowser.view.swing.BitmapUtil;
import com.teamdev.jxbrowser.view.swing.BrowserView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public abstract class AbstractDownloader {

    static String tmpDir=System.getProperty("java.io.tmpdir");
    int totalPage=0;
    String name;
    String docType;
    float widgetWidth;
    float widgetHeight;
    float windowWidth;
    float windowHeight;
    float pageWidth;
    float pageHeight;
    float pageLeftMargin;
    float screenScale;
    protected Browser browser;
    private BrowserView browserView;
    Component widget;


    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }


    public BrowserView getBrowserView() {
        return browserView;
    }

    public void setBrowserView(BrowserView browserView) {
        this.browserView = browserView;
    }

    public boolean allowFlash(){
        return false;
    }

    public abstract String getDocType();

    public abstract String getPageName();

    public abstract int  getPageCount();

    public abstract void download() throws Exception;

    public  void prepareDownload(String jsFile){
        InputStream is=AbstractDownloader.class.getClassLoader().getResourceAsStream(jsFile);
        String script="";
        if(is!=null){
            try(BufferedReader reader=new BufferedReader(new InputStreamReader(is))){
                String line=null;
                while((line=reader.readLine())!=null){
                    script+=line;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if(browser!=null){
                windowWidth=getJsFloat("window.outerWidth");//包含垂直滚动条
                windowHeight=getJsFloat("window.innerHeight");//不包含水平滚动条
                browser.mainFrame().get().executeJavaScript(script);

                widget = (Component) browserView.getComponent(0);
                widgetWidth=widget.getWidth();
                widgetHeight=widget.getHeight();

                screenScale= BitmapUtil.toBufferedImage(browser.bitmap()).getWidth()/widgetWidth;
            }
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
            Frame parent=Frame.getFrames().length>0?Frame.getFrames()[0]:null;
            JFileChooser dialog=new JFileChooser();
            dialog.setSelectedFile(new File(dialog.getCurrentDirectory()+File.separator+name+".pdf"));
            int ret=dialog.showSaveDialog(parent);
            if(ret==JFileChooser.APPROVE_OPTION){
                FileOutputStream fos = new FileOutputStream(dialog.getSelectedFile());

                Image first=Image.getInstance(tmpDir+File.separator+"1.png");
                com.itextpdf.text.Rectangle r=first.getWidth()>first.getHeight()? PageSize.A4.rotate():PageSize.A4;
                Document document = new Document(r,0,0,0,0);
                PdfWriter writer = PdfWriter.getInstance(document, fos);

                writer.open();
                document.open();

                float documentWidth = document.getPageSize().getWidth();
                float documentHeight = document.getPageSize().getHeight();

                for(int i=1;i<=totalPage;i++){
                    Image image=Image.getInstance(tmpDir+File.separator+i+".png");
                    image.scaleAbsolute(documentWidth, documentHeight);
                    document.add(image);
                }
                document.close();
                writer.close();
            }
        }else{
            JOptionPane.showMessageDialog(null,"不能生成PDF文件");
        }

    }
}
