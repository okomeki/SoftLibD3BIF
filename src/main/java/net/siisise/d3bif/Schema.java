package net.siisise.d3bif;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import net.siisise.json.JSONObject;

/**
 *
 * @author okome
 */
public interface Schema extends D3IfObject {
    
    Catalog getCatalog();
    Collection<? extends Table> tables() throws SQLException;

    Table create(BaseTable refTable, String... options) throws SQLException;
    Table create(Class struct, String... options) throws SQLException;
    Table create(String name, Map<String,Object> struct, String... options) throws SQLException;
    Table create(String name, JSONObject struct, String... options) throws SQLException;

    void drop(BaseTable table) throws SQLException;

    /**
     * インスタンス生成のみ
     * @param name
     * @return 仮のtable
     */
    Table newTable(String name);
    
    /**
     * クラスを対応するスキーマ以下用に置き換える定義のコピーっぽいもの
     * @param srcTable
     * @return
     * @throws SQLException 
     */
    Table newTable(BaseTable srcTable) throws SQLException;

    /**
     * キャッシュ利用かもしれないもの
     * @param name
     * @return
     * @throws SQLException 
     */
    Table cacheTable(String name) throws SQLException;

    /**
     * 現在値の参照
     * @param name
     * @return
     * @throws SQLException 
     */
    Table dbTable(String name) throws SQLException;
    
    // 何か databaseにつなぐだけ
    
    /**
     *
     * @param cmd
     * @param options
     * @throws SQLException
     */
    void sql(String cmd, String... options) throws SQLException;
    String preSQL(String cmd, String... options);

}
