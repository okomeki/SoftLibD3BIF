package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.where.Condition;

/**
 * TableからMapな要素だけ抽出中
 */
public interface MapTable extends RowTable {

    /**
     * 主キー、uniqueキーから自動的にConditionを生成したい
     * @param values
     * @return 
     * @throws java.sql.SQLException 
     */
    Condition key(Map<String,?> values) throws SQLException;
    Map<String,Object> map(ResultSet rs) throws SQLException;
    List<Map<String,Object>> map(Condition condition) throws SQLException;    
    
    /**
     * オブジェクトマッピングによるSQL 3つずつ
     * Bean/Map型のobjectをTableにマップする
     * 
     * @param values
     * @throws SQLException 
     */
    void insert(Map<String,Object> values) throws SQLException;
    
    void update(Map<String,Object> map) throws SQLException;
    /**
     * 
     * @param map 値
     * @param conditions 条件 where句 null可
     * @throws SQLException 
     */
    void update(Map<String,Object> map, Condition conditions) throws SQLException;
}
