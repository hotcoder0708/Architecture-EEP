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
    
    public static boolean big_switch = false; // Flag of the big switch
    
    Connection dbConn1 = null;           // MySQL connection handle for inventory
    Connection dbConn2 = null;           // MySQL connection handle for orderinfo
    Connection dbConn3 = null;           // MySQL connection handle for leaftech
    
    public SecureOperator() {
        super();
        
        initDB();
    }
    
    private void initDB() {
        //define the data source
        String SQLServerIP = "localhost";
        String sourceURL1 = "jdbc:mysql://" + SQLServerIP + ":3306/inventory";
        String sourceURL2 = "jdbc:mysql://" + SQLServerIP + ":3306/orderinfo";
        String sourceURL3 = "jdbc:mysql://" + SQLServerIP + ":3306/leaftech";
        try {
            //create a connection to the db
            dbConn1 = DriverManager.getConnection(sourceURL1,"remote","remote_pass");
        } catch (Exception e) {
            System.err.println("initDB:dbConn1 exception:" + e);
        }
        
        try {
            //create a connection to the db
            dbConn2 = DriverManager.getConnection(sourceURL2,"remote","remote_pass");
        } catch (Exception e) {
            System.err.println("initDB:dbConn2 exception:" + e);
        }
        
        try {
            //create a connection to the db
            dbConn3 = DriverManager.getConnection(sourceURL3,"remote","remote_pass");
        } catch (Exception e) {
            System.err.println("initDB:dbConn3 exception:" + e);
        }
    }

    /**
     * Authenticate a user.
     * @param username
     * @param password
     * @param role_type
     * @return -1 - login failure, 0 - success, 1 - this department APP is not authorized to you
     */
    @Override
    public int authenticate(String username, String password, int role_type) {
        if(dbConn1 != null) {
            try {
                String query = "SELECT * FROM user WHERE username = ? AND password = PASSWORD(?)";
                PreparedStatement ps = dbConn1.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    if(rs.getInt("role") == role_type) {
                        recordActivity(username, 1, 1);
                        return 0;
                    } else {
                        return 1;
                    }
                    
                } else {
                    recordActivity(username, 1, 0);
                }
            } catch(Exception e) {
                System.err.println("SecureOperator::authenticate exception:" + e);
            }
        }
        return -1;
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
                ps.execute();
            } catch(Exception e) {
                System.err.println("SecureOperator::recordActivity exception:" + e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean executeSQL(String sql) {
        Connection dbConn;
        if(sql.toLowerCase().contains("into order") || sql.toLowerCase().contains("table order")) { // orderinfo database
            dbConn = dbConn2;
        } else { // inventory database
            dbConn = dbConn1;
        }
                
        if(dbConn != null) {
            try {
                PreparedStatement ps = dbConn.prepareStatement(sql);
                ps.execute();
            } catch(Exception e) {
                System.err.println("SecureOperator::executeSQL exception:" + e);
                return false;
            }
        }
        
        if(!big_switch && dbConn == dbConn1 && !Utility.fromEEP(sql)) {
            // Before big_switch, we need to update both old leaftech database and the new duplicate
            // database
            if(dbConn3 != null) {
                try {
                    PreparedStatement ps = dbConn3.prepareStatement(sql);
                    ps.execute();
                } catch(Exception e) {
                    System.err.println("SecureOperator::executeSQL duplication part exception:" + e);
                    return false;
                }
            }
        }
        
        return true;
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
                System.err.println("SecureOperator::querySQL exception:" + e);
            }
        }
        return null;
    }
}
