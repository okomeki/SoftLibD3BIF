package net.siisise.d3bif;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import net.siisise.json.JSONObject;

/**
 *
 */
public interface Schema extends D3IfObject {
    
    Catalog getCatalog();

    /**
     * db と cache に分けるかも
     * @return
     * @throws SQLException 
     */
    Collection<? extends Table> dbTables() throws SQLException;
    Collection<? extends Index> dbIndexes() throws SQLException;
    Collection<? extends Sequence> dbSequences() throws SQLException;

    /**
     * 定義からテーブルを作る
     * @param refTable
     * @param options
     * @return
     * @throws SQLException 
     */
    Table createTable(BaseTable refTable, String... options) throws SQLException;

    /**
     * 構造的なクラスからテーブルを作る。
     * アノテーションもある
     * @param struct
     * @param options
     * @return
     * @throws SQLException
     */
    Table createTable(Class struct, String... options) throws SQLException;
    /**
     * Mapからテーブル定義を作る。
     * @param name
     * @param struct
     * @param options
     * @return
     * @throws SQLException 
     */
    Table createTable(String name, Map<String,Object> struct, String... options) throws SQLException;
    Table createTable(String name, JSONObject struct, String... options) throws SQLException;
    void drop(BaseTable table) throws SQLException;

    void drop(Index index) throws SQLException;
    Sequence createSequence(String name) throws SQLException;
    void drop(Sequence sequence) throws SQLException;

    /**
     * DB非連携インスタンス生成のみ
     * @param name
     * @return 仮のtable
     */
    Table newTable(String name);

    /**
     * DB非連携インスタンス生成のみ
     * @param name
     * @return 
     */
    Index newIndex(String name);

    /**
     * 
     * @param name
     * @return 
     */
    Sequence newSequence(String name);

    /**
     * 暫定
     * @param cls
     * @return 
     */
    Table newTable(Class cls);
    
    /**
     * クラスを対応するスキーマ以下用に置き換える定義のコピーっぽいもの
     * @param srcTable
     * @return
     * @throws SQLException 
     */
    Table newTable(BaseTable srcTable) throws SQLException;

    /**
     * キャッシュ利用かもしれないもの
     * ないときはdbTable直結か
     * @param name
     * @return
     * @throws SQLException 
     */
    Table cacheTable(String name) throws SQLException;
    Index cacheIndex(String name) throws SQLException;
    Sequence cacheSequence(String name) throws SQLException;

    /**
     * 現在値の参照
     * @param name
     * @return
     * @throws SQLException 
     */
    Table dbTable(String name) throws SQLException;
    Index dbIndex(String name) throws SQLException;
    /**
     * 
     * @param name
     * @return
     * @throws SQLException 
     */
    Sequence dbSequence(String name) throws SQLException;
    
    /**
     *
     * @param cmd
     * @param options
     * @throws SQLException
     */
    void sql(String cmd, String... options) throws SQLException;
    String preSQL(String cmd, String... options);

}
