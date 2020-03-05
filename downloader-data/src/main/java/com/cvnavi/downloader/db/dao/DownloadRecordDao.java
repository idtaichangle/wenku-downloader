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

    public static Collection<DownloadRecord> findByUrl(String url,Integer userId) {
        List<DownloadRecord> list=new ArrayList<>();
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from download_record where url=?";
                if(userId!=null){
                    sql+=" and user_id="+userId;
                }
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setString(1,url);
                ResultSet rs = ps.executeQuery();
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

    public static synchronized boolean insert(DownloadRecord record) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "insert into  download_record(user_id,url,create_time,file_id,payment_time) values(?,?,?,?,?)";
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
                String sql = "update  download_record set user_id=?, url=?,create_time=?,file_id=?,payment_time=? where id="+record.getId();
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
        st.setInt(1,recored.getUserId());
        st.setString(2,recored.getUrl());
        st.setLong(3,recored.getCreateTime());
        st.setInt(4,recored.getFileId());
        st.setLong(5,recored.getPaymentTime());
    }


    private static void fillValue(DownloadRecord dr,ResultSet rs) throws SQLException {
        dr.setId(rs.getInt("id"));
        dr.setUserId(rs.getInt("user_id"));
        dr.setUrl(rs.getString("url"));
        dr.setCreateTime(rs.getLong("create_time"));
        dr.setFileId(rs.getInt("file_id"));
        dr.setPaymentTime(rs.getLong("payment_time"));
    }

}
