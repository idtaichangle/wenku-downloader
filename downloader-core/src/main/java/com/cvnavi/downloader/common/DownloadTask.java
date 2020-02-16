package com.cvnavi.downloader.common;

import com.cvnavi.downloader.Config;
import com.cvnavi.downloader.base.AbstractDownloader;
import com.cvnavi.downloader.base.DownloaderSelector;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class DownloadTask {

    @Getter @Setter
    private int id;
    @Getter @Setter
    private String url;
    @Getter @Setter
    private File result;
    @Getter @Setter
    private DownloaderCallback callback;

    public void download(){
        AbstractDownloader downloader= DownloaderSelector.select(url,callback);
        if(downloader!=null){
            try {
                clearTmpDir();
                downloader.download();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    private void clearTmpDir()  {
        try {
            Files.list(Paths.get(Config.TMP_DIR)).forEach((f)->{
                try {
                    Files.delete(f);
                } catch (IOException e) {
                }
            });
        } catch (IOException e) {
        }
    }
}
