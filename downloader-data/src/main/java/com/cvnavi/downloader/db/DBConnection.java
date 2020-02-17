package com.cvnavi.downloader.db;

import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 数据库连接
 */
@Log4j2
public abstract class DBConnection {

    static {
        ServiceLoader<DbChecker> loader=ServiceLoader.load(DbChecker.class);
        Iterator<DbChecker> it=loader.iterator();
        while(it.hasNext()){
            try{
                it.next().checkTable();
            }catch (Exception e){
                log.error(e);
            }
        }
    }

    private static DBConnection inst;

    public static DBConnection getInstance() {
        if (inst == null) {
            if (JDBCConfig.dbDriver.contains("derby")) {
                inst = new DerbyConnection();
            }
        }

        return inst;
    }

    public abstract Connection get();

}
