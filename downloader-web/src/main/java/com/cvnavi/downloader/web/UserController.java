package com.cvnavi.downloader.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class UserController extends BaseController {

    @RequestMapping("/login")
    public Object login(String email,String password){
        return result(true," 登录成功");
    }
}