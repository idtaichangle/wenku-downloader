package com.cvnavi.downloader.db.model;

import lombok.Data;

@Data
public class DownloadRecord {
    private int id;
    private String url;
    private long createTime;
    private String name;
    private String encryptName;
}
