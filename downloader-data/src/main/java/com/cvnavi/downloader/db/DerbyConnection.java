package com.cvnavi.downloader.db;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * java内置数据库derby。
 */
@Log4j2
public class DerbyConnection extends DBConnection {

    protected static Connection con;

    static {
        String home=System.getProperty("APP_HOME");
        if(home!=null){
            String dbHome=home+File.separator+"db";
            File d=new File(dbHome);
            if(!d.exists()){
                d.mkdir();
            }
            System.setProperty("derby.system.home", dbHome);
        }else{
            System.setProperty("derby.system.home", System.getProperty("user.home") + File.separator + ".derby");
        }
    }

    /**
     * 获取数据库连接。使用完毕后，可以不用关闭连接。web app销毁时会关闭连接。
     *
     * @return
     */
    @Override
    public Connection get() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName(JDBCConfig.dbDriver);
                con = DriverManager.getConnection(JDBCConfig.dbUrl, JDBCConfig.dbUser, JDBCConfig.dbPassword);
            }
        } catch (Exception e) {
            log.error(e);
        }

        return con;
    }
}
