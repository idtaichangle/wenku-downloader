package com.cvnavi.downloader.db.dao;

import com.cvnavi.downloader.db.DBConnection;
import com.cvnavi.downloader.db.model.DownloadRecord;
import lombok.extern.log4j.Log4j2;

import java.sql.*;

@Log4j2
public class DownloadRecordDao {

    public static DownloadRecord findDownloadRecord(int id) {
        DownloadRecord dr=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from download_record where id="+id;
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    dr=new DownloadRecord();
                    fillValue(dr,rs);
                    break;
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return dr;
    }

    public static synchronized boolean insertDownloadRecord(DownloadRecord record) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "insert into  download_record(url,create_time) values(?,?)";
                PreparedStatement st = con.prepareStatement(sql);
                setStatement(st, record);
                st.execute();
                st = con.prepareStatement("select max(id) from download_record");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    record.setId(rs.getInt(1));
                    break;
                }
                st.close();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static void setStatement(PreparedStatement st, DownloadRecord recored) throws SQLException {
        st.setString(1,recored.getUrl());
        st.setLong(2,recored.getCreateTime());
    }


    private static void fillValue(DownloadRecord dr,ResultSet rs) throws SQLException {
        dr.setId(rs.getInt("id"));
        dr.setUrl(rs.getString("url"));
        dr.setCreateTime(rs.getLong("create_time"));
    }
}
