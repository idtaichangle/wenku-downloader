package com.cvnavi.downloader.common;

import com.cvnavi.downloader.Document;

public interface DownloaderCallback {
    void metaReady(DownloadTask task, Document.Meta meta);
    void downloadFinish(DownloadTask task, boolean success);
}
