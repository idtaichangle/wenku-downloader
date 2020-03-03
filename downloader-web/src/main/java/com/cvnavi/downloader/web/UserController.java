package com.cvnavi.downloader.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Log4j2
public class UserController extends BaseController {

    @RequestMapping("/login")
    public Object login(String email, String password, HttpSession session){
        session.setAttribute("email",email);
        return result(true," 登录成功");
    }

    @RequestMapping("/logout")
    public Object logout(HttpSession session){
        session.removeAttribute("email");
        return result(true," 退出成功");
    }
}
