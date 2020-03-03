package com.cvnavi.downloader.web;

import com.cvnavi.downloader.db.dao.UserDao;
import com.cvnavi.downloader.db.model.User;
import com.cvnavi.downloader.util.EncryptUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Log4j2
public class UserController extends BaseController {

    @RequestMapping("/login")
    public Object login(String email, String password, HttpSession session){
        if(StringUtils.isEmpty(email) ){
            return result(false," 邮箱不能为空");
        }
        if(StringUtils.isEmpty(password) ){
            return result(false," 密码不能为空");
        }
        User user= UserDao.findByEmail(email);
        if(user!=null && EncryptUtil.md5(password).equals(user.getPassword())){
            session.setAttribute("email",email);
            return result(true," 登录成功");
        }
        return result(false," 登录失败");
    }

    @RequestMapping("/logout")
    public Object logout(HttpSession session){
        session.removeAttribute("email");
        return result(true," 退出成功");
    }


    @RequestMapping("/register")
    public Object register(String email, String password){
        if(StringUtils.isEmpty(email) ){
            return result(false," 邮箱不能为空");
        }
        if(StringUtils.isEmpty(password) ){
            return result(false," 密码不能为空");
        }
        User user= UserDao.findByEmail(email);
        if(user!=null){
            return result(false," 邮箱已经被注册");
        }

        user=new User();
        user.setEmail(email);
        user.setPassword(EncryptUtil.md5(password));
        UserDao.insert(user);

        return result(true," 注册成功");
    }
}
