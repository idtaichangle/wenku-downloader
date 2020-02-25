package com.cvnavi.downloader.ocr;

import com.cvnavi.downloader.core.DownloadTask;
import com.cvnavi.downloader.core.DownloaderCallback;
import com.cvnavi.downloader.util.EncryptUtil;
import com.cvnavi.downloader.util.ResourceReader;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class OcrTask {
    public static final String ACROBAT_EXE="C:\\Program Files (x86)\\Adobe\\Acrobat DC\\Acrobat\\Acrobat.exe";
    public static String TMP_DIR=System.getProperty("java.io.tmpdir")+"wenku-ocr";

    private DownloadTask downloadTask;

    public OcrTask(DownloadTask downloadTask) {
        this.downloadTask=downloadTask;
    }

    public static boolean existAcrobat(){
        return Files.isExecutable(Paths.get(ACROBAT_EXE));
    }

    private void prepare(){
        Path dir=Paths.get(TMP_DIR);
        try {
            if(!Files.exists(dir)){
                Files.createDirectories(dir);
            }
            Files.list(dir).forEach((f)->{
                try {
                    Files.delete(f);
                } catch (IOException e) {
                }
            });

            Path sequ=Paths.get(dir.toFile()+ File.separator+"ocr.sequ");
            Files.createFile(sequ);
            String s=new String(ResourceReader.readFile("ocr.sequ"));
            String replaced="";
            Matcher m= Pattern.compile("File path=\".*\"").matcher(s);
            if(m.find()){
                replaced=s.substring(0,m.start());

                replaced+="File path=\"/"
                        +downloadTask.getResult().getFile().replace(":","").replace("\\","/")
                        +"\"";
                replaced+=s.substring(m.end());
            }
            Files.write(sequ,replaced.getBytes());


            Path bat=Paths.get(dir.toFile()+File.separator+"ocr.bat");
            Files.createFile(bat);
            List<String> lines= ResourceReader.readLines("ocr.bat");
            lines.set(0,"SET AUTOBATCHSEQU="+TMP_DIR+File.separator+"ocr.sequ");
            Files.write(bat,lines);

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public void doPdfOcr(){
        if(existAcrobat()){
            log.info("begin ocr task.");
            prepare();
            ProcessBuilder pb=new ProcessBuilder();
            Map<String, String> env = pb.environment();
            pb.command(TMP_DIR+File.separator+"ocr.bat");
            try {
                Process p=pb.start();
                p.waitFor();
                log.info("ocr success.");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        if(downloadTask.getCallback()!=null){
            downloadTask.getCallback().downloadFinish(downloadTask.getId(),true, EncryptUtil.md5(downloadTask.getUrl()));
        }
    }
}
