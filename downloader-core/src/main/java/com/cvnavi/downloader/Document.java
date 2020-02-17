package com.cvnavi.downloader;

import lombok.Data;

@Data
public class Document {

    private String file;
    private Meta meta;

    @Data
    public static class Meta{
        private String name;
        private int totalPage;
        private String type="pdf";
    }
}
