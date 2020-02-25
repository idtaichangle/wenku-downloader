package com.cvnavi.downloader.core;

import com.cvnavi.downloader.Document;

public interface DownloaderCallback {
    void metaReady(int taskId, Document.Meta meta);
    void downloadFinish(int taskId, boolean success,String fileName);
}
