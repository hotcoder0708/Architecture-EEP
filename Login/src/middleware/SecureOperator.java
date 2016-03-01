/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import communication.SecureOperate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author netdong
 */
public class SecureOperator implements SecureOperate {
    
    Connection dbConn1 = null;           // MySQL connection handle for inventory
    Connection dbConn2 = null;           // MySQL connection handle for orderinfo
    
    public SecureOperator() {
        super();
        
        initDB();
    }
    
    private void initDB() {
        //define the data source
        String SQLServerIP = "localhost";
        String sourceURL1 = "jdbc:mysql://" + SQLServerIP + ":3306/inventory";
        String sourceURL2 = "jdbc:mysql://" + SQLServerIP + ":3306/orderinfo";
        try {
            //create a connection to the db
            dbConn1 = DriverManager.getConnection(sourceURL1,"remote","remote_pass");
        } catch (Exception e) {
            System.err.println("initDB:dbConn1 exception:" + e);
            e.printStackTrace();
        }
        
        try {
            //create a connection to the db
            dbConn2 = DriverManager.getConnection(sourceURL2,"remote","remote_pass");
        } catch (Exception e) {
            System.err.println("initDB:dbConn2 exception:" + e);
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        if(dbConn1 != null) {
            try {
                String query = "SELECT * FROM user WHERE username = ? AND password = PASSWORD(?)";
                PreparedStatement ps = dbConn1.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    recordActivity(username, 1, 1);
                    return true;
                } else {
                    recordActivity(username, 1, 0);
                }
            } catch(Exception e) {
                System.err.println("SecureOperator::authenticate exception:" + e);
            }
        }
        return false;
    }

    @Override
    public boolean recordActivity(String username, int activity_type, int success) {
        if(dbConn1 != null) {
            try {
                String query = "INSERT INTO activity (username, activity_type, success, activity_time) VALUES(?, ?, ?, CURRENT_TIMESTAMP)";
                PreparedStatement ps = dbConn1.prepareStatement(query);
                ps.setString(1, username);
                ps.setInt(2, activity_type);
                ps.setInt(3, success);
                return ps.execute();
            } catch(Exception e) {
                System.err.println("SecureOperator::authenticate exception:" + e);
            }
        }
        return false;
    }

    @Override
    public boolean executeSQL(String sql) {
        Connection dbConn;
        if(sql.toLowerCase().contains("orders")) { // orderinfo database
            dbConn = dbConn2;
        } else { // inventory database
            dbConn = dbConn1;
        }
        
        if(dbConn != null) {
            try {
                PreparedStatement ps = dbConn.prepareStatement(sql);
                return ps.execute();
            } catch(Exception e) {
                System.err.println("SecureOperator::authenticate exception:" + e);
            }
        }
        return false;
    }

    @Override
    public ResultSet querySQL(String sql) {
        Connection dbConn;
        if(sql.toLowerCase().contains("orders")) { // orderinfo database
            dbConn = dbConn2;
        } else { // inventory database
            dbConn = dbConn1;
        }
        
        if(dbConn != null) {
            try {
                PreparedStatement ps = dbConn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                return rs;
            } catch(Exception e) {
                System.err.println("SecureOperator::authenticate exception:" + e);
            }
        }
        return null;
    }
}
