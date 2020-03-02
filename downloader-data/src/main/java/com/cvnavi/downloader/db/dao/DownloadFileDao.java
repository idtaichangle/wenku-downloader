package com.cvnavi.downloader.db.dao;

import com.cvnavi.downloader.db.DBConnection;
import com.cvnavi.downloader.db.model.DownloadFile;
import com.cvnavi.downloader.db.model.DownloadRecord;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class DownloadFileDao {


    public static Collection<DownloadFile> list(int pageIndex, int pageSize) {
        List<DownloadFile> list=new ArrayList<>();
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = " select * from download_file order by id desc OFFSET "+((pageIndex-1)*pageSize)+" ROWS FETCH NEXT "+pageSize+" ROWS ONLY";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    DownloadFile dr=new DownloadFile();
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

    public static DownloadFile find(int id) {
        DownloadFile df=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from download_file where id="+id;
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    df=new DownloadFile();
                    fillValue(df,rs);
                    break;
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return df;
    }

    public static DownloadFile findByUrl(String url) {
        DownloadFile df=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from download_file where url=?";
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setString(1,url);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    df=new DownloadFile();
                    fillValue(df,rs);
                    break;
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return df;
    }

    public static DownloadFile findByEncryptName(String name) {
        DownloadFile df=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from download_file where encrypt_name=?";
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setString(1,name);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    df=new DownloadFile();
                    fillValue(df,rs);
                    break;
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return df;
    }

    public static synchronized boolean insert(DownloadFile file) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "insert into  download_file(url,create_time,name,encrypt_name,type,total_pages) values(?,?,?,?,?,?)";
                PreparedStatement st = con.prepareStatement(sql);
                setStatement(st, file);
                st.execute();
                st = con.prepareStatement("select max(id) from download_file");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    file.setId(rs.getInt(1));
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

    public static synchronized boolean update(DownloadFile file) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "update  download_file set url=?,create_time=?,name=?,encrypt_name=?,type=?,total_pages=? where id="+file.getId();
                PreparedStatement st = con.prepareStatement(sql);
                setStatement(st, file);
                st.execute();
                st.close();
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private static void setStatement(PreparedStatement st, DownloadFile file) throws SQLException {
        st.setString(1,file.getUrl());
        st.setLong(2,file.getCreateTime());
        st.setString(3,file.getName());
        st.setString(4,file.getEncryptName());
        st.setString(5, file.getType());
        st.setInt(6,file.getTotalPages());
    }


    private static void fillValue(DownloadFile df,ResultSet rs) throws SQLException {
        df.setId(rs.getInt("id"));
        df.setUrl(rs.getString("url"));
        df.setCreateTime(rs.getLong("create_time"));
        df.setName(rs.getString("name"));
        df.setEncryptName(rs.getString("encrypt_name"));
        df.setType(rs.getString("type"));
        df.setTotalPages(rs.getInt("total_pages"));
    }

}
