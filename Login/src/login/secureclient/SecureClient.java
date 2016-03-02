/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login.secureclient;

import communication.SecureOperate;
import java.sql.ResultSet;
import middleware.SecureOperator;

/**
 *
 * @author netdong
 */
public class SecureClient {
    private static SecureClient instance = null;
    SecureOperate so; // Client secure operator
    
    protected SecureClient() {
        // No direct external instantiation, only internal
        super();
        
        init();
    }
    
    public static SecureClient getInstance() {
        if(instance == null) {
            instance = new SecureClient();
        }
        return instance;
    }
    
    private void init() {
        so = new SecureOperator();
    }
    /**
     * Authenticate a user through middle-ware.
     * @param username
     * @param password
     * @param role_type
     * @return -1 - login failure, 0 - success, 1 - this department APP is not authorized to you
     */
    public int authenticate(String username, String password, int role_type) {
        return so.authenticate(username, password, role_type);
    }
    
    /**
     * Record user activities.
     * @param username
     * @param activity_type 0-login, 1-logout
     * @param success 1-success, 0-failure
     * @return true if recored successfully, otherwise false
     */
    public boolean recordActivity(String username, int activity_type, int success) {
        return so.recordActivity(username, activity_type, success);
    }
    
    /**
     * SQL Execute from middle-ware.
     * @param sql
     * @return true if executed successfully, otherwise false
     */
    public boolean executeSQL(String sql) {
        return so.executeSQL(sql);
    }
    
    /**
     * SQL Query results from middle-ware.
     * @param sql
     * @return 
     */
    public ResultSet querySQL(String sql) {
        return so.querySQL(sql);
    }
}
