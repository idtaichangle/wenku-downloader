package com.cvnavi.downloader.db;

import com.cvnavi.downloader.util.ResourceReader;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件里的jdbc连接属性
 */

@Log4j2
public class JDBCConfig {

    public static String dbDriver;
    public static String dbUrl;
    public static String dbUser;
    public static String dbPassword;


    static {
        Properties p = ResourceReader.readProperties("jdbc.properties");
        dbDriver = p.getProperty("db.driver");
        dbUrl = p.getProperty("db.url");
        dbUser = p.getProperty("db.user");
        dbPassword = p.getProperty("db.password");
    }
}
