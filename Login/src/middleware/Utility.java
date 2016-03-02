/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

/**
 *
 * @author netdong
 */
public class Utility {
    public static String[] INVENTORY_TABLES = {
        "trees", "shrubs", "seeds", "cultureboxes", "genomics", "processing", "referencematerials"
    };
    public static String[] INVENTORY_TABLE_NAMES = {
        "TREE", "SHRUB", "SEED", "CULTURE BOXES", "GENOMICS", "PROCESSING", "REFERENCE MATERIALS"
    };
    
    public static boolean fromEEP(String table_name) {
        return (table_name.equals("trees") || table_name.equals("shrubs") || table_name.equals("seeds"));
    }
    
    public static boolean fromEEPBySQL(String sql) {
        for(int i=0; i<3; i++) {
            String kw1 = "update " + INVENTORY_TABLES[i];
            String kw2 = "into " + INVENTORY_TABLES[i];
            if(sql.toLowerCase().contains(kw1) || sql.toLowerCase().contains(kw2)) {
                return true;
            }
        }
        return false;
    }
}
