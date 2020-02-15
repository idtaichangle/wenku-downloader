package com.cvnavi.downloader.web;

import com.cvnavi.downloader.BrowserFrame;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController extends BaseController{

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/preview",method = RequestMethod.POST)
    public Object preview(String url){
        Application.browser.browse(url);
        return result(true);
    }

    @RequestMapping(value = "/submit_download",method = RequestMethod.POST)
    public Object submitDownload(String url){
        Application.browser.download();
        return result(true);
    }
}
