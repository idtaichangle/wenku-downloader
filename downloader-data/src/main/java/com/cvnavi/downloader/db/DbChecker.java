package com.cvnavi.downloader.db;

import java.sql.ResultSet;

/**
 * 检查数据表是否存在 。如果不存在 ，则创建表。
 */
public abstract class DbChecker {
    public abstract void checkTable();

    /**
     * 判断表是否存在
     * @param name
     * @return
     * @throws Exception
     */
    protected boolean existTable(String name) throws Exception {
        ResultSet rs = DBConnection.getInstance().get().getMetaData().getTables(null, null, null, null);
        while (rs.next()) {
            String s = rs.getString("TABLE_NAME");
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
