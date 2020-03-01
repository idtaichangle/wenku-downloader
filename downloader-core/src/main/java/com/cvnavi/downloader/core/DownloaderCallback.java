package com.cvnavi.downloader.core;

import com.cvnavi.downloader.Document;

public interface DownloaderCallback {
    void metaReady(int taskId, Document.Meta meta);
    void documentReady(int taskId, boolean success, String fileName);
}
