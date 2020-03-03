package com.cvnavi.downloader.db.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String email;
    private String password;
    private long createTime;
}
