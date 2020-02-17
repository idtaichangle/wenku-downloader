package com.cvnavi.downloader.db;

import com.cvnavi.downloader.util.ResourceReader;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.StringReader;


/**
 * 检查数据库是否存在。如果不存在，自动创建库和表。
 */
@Log4j2
public class DefaultDbChecker extends DbChecker {
    public  void checkTable() {
        try {
            if (!existTable("download_record")) {

                ScriptRunner runner=new ScriptRunner(DBConnection.getInstance().get(),true,true);
                String sql = new String(ResourceReader.readFile("create_table.sql"));
                runner.runScript(new BufferedReader(new StringReader(sql)));
                log.info("create table in derby.");
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }

//    public static void closeDatabase() {
//        if (DBConnection.getInstance() instanceof DerbyConnection) {
//            try {
//                DriverManager.getConnection(JDBCConfig.dbUrl + ";shutdown=true");
//            } catch (SQLException e) {
//                log.error(e);
//            }
//        }
//    }

}
