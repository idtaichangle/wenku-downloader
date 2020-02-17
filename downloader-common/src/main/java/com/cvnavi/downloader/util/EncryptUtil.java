package com.cvnavi.downloader.util;

import lombok.extern.log4j.Log4j2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log4j2
public class EncryptUtil {
    /**
     * 将一个字符串生成对于的md5
     * @param src
     * @return
     */
    public static String md5(String src){
        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = src.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < thedigest.length; i++) {
                sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }
        return null;
    }
}
