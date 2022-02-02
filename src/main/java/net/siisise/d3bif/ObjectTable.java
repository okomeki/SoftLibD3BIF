package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.siisise.d3bif.where.Condition;
import net.siisise.json.JSON2Object;

/**
 * TableからObjectなところだけ分離してみる
 * 実装はJsonTableの拡張
 * @param <E>
 */
public interface ObjectTable<E> extends RowTable {
    
    @Override
    Class getObjectClass();

    /**
     * 主キー、uniqueキーから自動的にConditionを生成したい
     * @param obj
     * @return 
     * @throws java.sql.SQLException 
     */
    Condition key(E obj) throws SQLException;
    
    E obj(ResultSet rs) throws SQLException;
    E obj(JSON2Object json) throws SQLException;
    List<E> obj(Condition condition) throws SQLException;

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
