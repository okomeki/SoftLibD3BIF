package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.siisise.d3bif.where.Condition;

/**
 * TableからObjectなところだけ分離してみる
 * 実装はJsonTableの拡張
 * @author okome
 * @param <E>
 */
public interface ObjectTable<E> extends RowTable {
    
    /**
     * 主キー、uniqueキーから自動的にConditionを生成したい
     * @param obj
     * @return 
     * @throws java.sql.SQLException 
     */
    Condition key(E obj) throws SQLException;
    
    E obj(ResultSet rs) throws SQLException;

    /**
     * オブジェクトマッピングによるSQL 3つずつ
     * Bean/Map型のobjectをTableにマップする
     * 
     * @param obj
     * @throws SQLException 
     */
    void insert(E obj) throws SQLException;

    /**
     * 
     * @param obj 値 Beanくらい
     * @param conditions 条件 where句 null可
     * @throws SQLException 
     */
    void update(E obj, Condition conditions) throws SQLException;
    void update(E obj) throws SQLException;
}
