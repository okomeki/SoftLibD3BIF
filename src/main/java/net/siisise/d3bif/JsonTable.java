package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.siisise.d3bif.where.Condition;
import net.siisise.json.JSONObject;

/**
 * 実装はMapTableの拡張
 */
public interface JsonTable extends RowTable {

    /**
     * 主キー、uniqueキーから自動的にConditionを生成したい
     * @param json
     * @return 
     * @throws java.sql.SQLException 
     */
    Condition key(JSONObject json) throws SQLException;
    JSONObject json(ResultSet rs) throws SQLException;

    /**
     * オブジェクトマッピングによるSQL 3つずつ
     * Bean/Map型のobjectをTableにマップする
     * 
     * @param json
     * @throws SQLException 
     */
    void insert(JSONObject json) throws SQLException;

    /**
     * ひとつの例
     * XMLなどでもよかった?
     * @param json 値
     * @param conditions 条件 where句 null可
     * @throws SQLException 
     */
    void update(JSONObject json, Condition conditions) throws SQLException;
    void update(JSONObject json) throws SQLException;
    
}
