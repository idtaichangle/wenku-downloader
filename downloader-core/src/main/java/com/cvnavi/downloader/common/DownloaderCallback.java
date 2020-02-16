package com.cvnavi.downloader.common;

public interface DownloaderCallback {
    void downloadFinish(DownloadTask task, boolean success);
}
