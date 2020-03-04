package com.cvnavi.downloader.db.model;

import lombok.Data;

@Data
public class DownloadRecord {
    private int id;
    private int userId;
    private String url;
    private long createTime;
    private int fileId;
    private long paymentTime;
}
