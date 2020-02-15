package com.cvnavi.downloader.web;


import java.util.HashMap;

public class BaseController {
    public HashMap<String,Object> result(boolean success){
        HashMap<String,Object> map=new HashMap<>();
        map.put("success",success);
        return map;
    }

    public HashMap<String,Object> result(boolean success,String msg){
        HashMap<String,Object> map=new HashMap<>();
        map.put("success",success);
        map.put("msg",msg);
        return map;
    }
}
