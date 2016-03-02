/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.sql.ResultSet;

/**
 *
 * @author netdong
 */
public interface SecureOperate {
    int authenticate(String username, String password, int role_type);
    boolean recordActivity(String username, int activity_type, int success);
    boolean executeSQL(String sql);
    ResultSet querySQL(String sql);
}
