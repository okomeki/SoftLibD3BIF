package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.siisise.d3bif.where.Condition;

/**
 * データ操作系の基礎Table
 * @author okome
 */
public interface RowTable extends BaseTable {
    
    /**
     * ど?
     * @param conditions 条件 where句 null可 主キー的なもの
     * @return 
     * @throws SQLException
     */
    PreUpdate update(Condition conditions) throws SQLException;
    
    void remove(Condition conditions) throws SQLException;

    /**
     * 
     * @param conditions andかorどちらがいいのか
     * @return ふつーのResultSet
     * @throws SQLException 
     */
    ResultSet query(Condition conditions) throws SQLException;
    ResultSet query() throws SQLException;

    // key,insert,updateは ObjectTable,MapTable,JsonTable へ
    // 更新
    
    int size() throws SQLException;
    int count(Condition condition) throws SQLException;
}
