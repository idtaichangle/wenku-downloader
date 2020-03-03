package com.cvnavi.downloader.db.dao;

import com.cvnavi.downloader.db.DBConnection;
import com.cvnavi.downloader.db.model.User;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class UserDao {


    public static Collection<User> list(int pageIndex, int pageSize) {
        List<User> list=new ArrayList<>();
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = " select * from users order by id desc OFFSET "+((pageIndex-1)*pageSize)+" ROWS FETCH NEXT "+pageSize+" ROWS ONLY";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    User u=new User();
                    fillValue(u,rs);
                    list.add(u);
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return list;
    }

    public static User find(int id) {
        User user=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from users where id="+id;
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    user=new User();
                    fillValue(user,rs);
                    break;
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return user;
    }

    public static User findByEmail(String email) {
        User user=null;
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                Statement st = con.createStatement();
                String sql = "select * from users where email=?";
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setString(1,email);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    user=new User();
                    fillValue(user,rs);
                    break;
                }
                rs.close();
                st.close();
            }
        } catch (Exception e) {
            log.error(e);
        }
        return user;
    }

    public static synchronized boolean insert(User user) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "insert into  users(email,password,create_time) values(?,?,?)";
                PreparedStatement st = con.prepareStatement(sql);
                setStatement(st, user);
                st.execute();
                st = con.prepareStatement("select max(id) from users");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    user.setId(rs.getInt(1));
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

    public static synchronized boolean update(User user) {
        try {
            Connection con = DBConnection.getInstance().get();
            if (con != null) {
                String sql = "update  users set email=?,password=?,create_time=? where id="+user.getId();
                PreparedStatement st = con.prepareStatement(sql);
                setStatement(st, user);
                st.execute();
                st.close();
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private static void setStatement(PreparedStatement st, User user) throws SQLException {
        st.setString(1,user.getEmail());
        st.setString(2,user.getPassword());
        st.setLong(3,user.getCreateTime());
    }


    private static void fillValue(User user,ResultSet rs) throws SQLException {
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setCreateTime(rs.getLong("create_time"));
    }

}
