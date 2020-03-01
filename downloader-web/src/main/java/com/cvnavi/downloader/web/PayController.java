package com.cvnavi.downloader.web;

import com.cvnavi.downloader.db.dao.DownloadRecordDao;
import com.cvnavi.downloader.db.model.DownloadRecord;
import com.cvnavi.downloader.util.EncryptUtil;
import com.cvnavi.downloader.util.HttpRequestUtil;
import com.cvnavi.downloader.web.ws.WebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
public class PayController  extends BaseController{

    @RequestMapping(value = "/createPayOrder",method = RequestMethod.POST)
    public Object createPayOrder(int taskId,int payType){
        DownloadRecord record= DownloadRecordDao.find(taskId);

        String url=Application.SYSTEM_PROPERTIES.getProperty("pay.create.order");
        String payId=System.currentTimeMillis()+"";
        String type=payType+"";//1=wx,2=alipay
        String price="1.00";
        String param=record.getId()+"";
        String sign= EncryptUtil.md5(payId+param+type+price+Application.SYSTEM_PROPERTIES.getProperty("pay.secret"));
        String s="payId="+payId+"&type="+type+"&price="+price+"&sign="+sign+"&param="+param+"&isHtml=0";
        String result=HttpRequestUtil.sendPost(url,s);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map=mapper.readValue(result, Map.class);
            if(1==(Integer)map.get("code")){
                HashMap<String,Object> m2= (HashMap<String, Object>) map.get("data");
                HashMap<String,Object> data=new HashMap<>();
                String img="img/"+(payType==1?"weixin":"alipay")+"/"+m2.get("reallyPrice")+".jpg";
                data.put("qr_img",img);
                return result(true,"创建成功",data);
            }

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
        }

        log.info(result);

        return result(false,"创建失败");
    }


    @RequestMapping(value = "/payNotify")
    public Object payNotify(String payId,int type,float price,float reallyPrice,String param,String sign) {

        WebSocketServer.payCallback.payResult(true,Integer.parseInt(param));
        return "success";
    }

    @RequestMapping(value = "/payResult",method = RequestMethod.GET)
    public ModelAndView payResult(){
        return new ModelAndView("pay");
    }

}
