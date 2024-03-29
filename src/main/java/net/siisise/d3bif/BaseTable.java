package net.siisise.d3bif;

import java.sql.SQLException;
import java.util.List;

/**
 * 行(データ)と列(定義)を持つかもしれない
 * BaseTableはデータ操作機能を持たない
 * @author okome
 */
public interface BaseTable extends D3IfObject {

    Schema getSchema();
    List<Column> columns() throws SQLException;

    Column newColumn(String name);
    Column col(String name);
    /**
     * 複製
     * @param srcColumn
     * @return 
     */
    Column col(Column srcColumn);

    Class getObjectClass();

    /**
     * 定義、型の変換のみ
     * @param srcTable
     * @throws SQLException 
     */
    void copy(BaseTable srcTable) throws SQLException;
    
    void drop() throws SQLException;
    
    // 多要素ないろいろ
    
    /**
     * 主キー
     * Uniqueもほしい?
     * @return 順序も大事らしい
     * @throws SQLException 
     */
    List<Column> primaryKeys() throws SQLException;
    /**
     * 主キーではないもの
     * @return
     * @throws SQLException 
     */
    List<Column> uniqueKeys() throws SQLException;

    /**
     * 外部キー
     * @return
     * @throws SQLException 
     */
    List<Column> importedKeys() throws SQLException;
}
