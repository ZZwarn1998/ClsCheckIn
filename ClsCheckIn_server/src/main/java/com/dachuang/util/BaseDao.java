package com.dachuang.util;

import com.sun.javafx.collections.MappingChange;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao {
    // private static String JDBC_DRIVER ="com.mysql.jdbc.Driver";  // If the version of mysql is lower than 8.0.0;
    private static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  // If the version of mysql is higher than or equal to 8.0.0;
    private static String URL ="jdbc:mysql://localhost:3306/cls_check_in_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USER ="";
    private static String PWD ="";

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PWD);
    }

    public static void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if(rs!=null) {
            rs.close();
        }
        if(stmt!=null) {
            stmt.close();
        }
        if(conn!=null) {
            conn.close();
        }
    }


    public List<Map<String, Object>> executeQuerySQL(String preparedSql, Object[] param) throws ClassNotFoundException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(preparedSql);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]);
                }
            }
            rs = pstmt.executeQuery(); // Execute sql statement
            ResultSetMetaData rsmd = rs.getMetaData();
            int col_num = rsmd.getColumnCount();
            while(rs.next()){
                Map<String, Object> map = new HashMap<>();
                for(int i = 1; i <= col_num; ++i) {
                    String label = rsmd.getColumnLabel(i);
                    map.put(label, rs.getObject(label));
                }
                result.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                BaseDao.closeAll(conn, pstmt, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean executeUpdateSQL(String preparedSql, Object[] param){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean result = false;
        int cnt = 0;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(preparedSql);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]);
                }
            }
            System.out.println(pstmt.toString());
            cnt = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                BaseDao.closeAll(conn, pstmt, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(cnt!=0){
            return true;
        }else{
            return false;
        }
    }

}
