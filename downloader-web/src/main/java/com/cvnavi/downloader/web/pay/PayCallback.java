package com.cvnavi.downloader.web.pay;

public interface PayCallback {
    void payResult(boolean success,int taskId);
}
