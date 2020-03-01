package com.cvnavi.downloader.db.dao;

import com.cvnavi.downloader.db.DBConnection;
import com.cvnavi.downloader.db.model.DownloadRecord;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class DownloadRecordDao {


    public static Collection<DownloadRecord> list(int pageIndex, int pageSize) {
        List<DownloadRecord> list=new ArrayList<>();
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = " select * from download_record order by id desc OFFSET "+((pageIndex-1)*pageSize)+" ROWS FETCH NEXT "+pageSize+" ROWS ONLY";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    DownloadRecord dr=new DownloadRecord();
                    fillValue(dr,rs);
                    list.add(dr);
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return list;
    }

    public static DownloadRecord find(int id) {
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

    public static DownloadRecord findByEncryptName(String name) {
        DownloadRecord dr=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from download_record where encrypt_name='"+name+"'";
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

    public static synchronized boolean insert(DownloadRecord record) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "insert into  download_record(url,create_time,name,encrypt_name) values(?,?,?,?)";
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
            log.error(e.getMessage());
        }
        return false;
    }

    public static synchronized boolean update(DownloadRecord record) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "update  download_record set url=?,create_time=?,name=?,encrypt_name=? where id="+record.getId();
                PreparedStatement st = con.prepareStatement(sql);
                setStatement(st, record);
                st.execute();
                st.close();
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private static void setStatement(PreparedStatement st, DownloadRecord recored) throws SQLException {
        st.setString(1,recored.getUrl());
        st.setLong(2,recored.getCreateTime());
        st.setString(3,recored.getName());
        st.setString(4,recored.getEncryptName());
    }


    private static void fillValue(DownloadRecord dr,ResultSet rs) throws SQLException {
        dr.setId(rs.getInt("id"));
        dr.setUrl(rs.getString("url"));
        dr.setCreateTime(rs.getLong("create_time"));
        dr.setName(rs.getString("name"));
        dr.setEncryptName(rs.getString("encrypt_name"));
    }

}
