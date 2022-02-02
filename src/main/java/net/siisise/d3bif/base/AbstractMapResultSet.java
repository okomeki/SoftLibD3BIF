package net.siisise.d3bif.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import net.siisise.d3bif.where.Condition;
import net.siisise.d3bif.MapResultSet;
import net.siisise.json.JSON2;
import net.siisise.json.JSON2Object;

/**
 *
 * @param <E>
 */
public class AbstractMapResultSet<E> implements MapResultSet<E> {
    AbstractTable<E> table;
    ResultSet rs;

    AbstractMapResultSet(AbstractTable<E> table, ResultSet rs) {
        this.table = table;
        this.rs = rs;
    }
    
    public AbstractMapResultSet(AbstractTable<E> table, Condition condition) throws SQLException {
        this(table, table.query(condition));
    }

    public AbstractMapResultSet(AbstractTable<E> table) throws SQLException {
        this(table, table.query());
    }

    @Override
    public boolean next() throws SQLException {
        return rs.next();
    }

    /**
     *
     * @see
     * net.siisise.d3bif.remote.RemoteTable#setList(java.sql.PreparedStatement,
     * java.util.List, java.util.Map)
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Object> map() throws SQLException {
        return table.map(rs);
    }

    /**
     * 取得時は外部参照込み
     *
     * @return
     * @throws SQLException
     */
    @Override
    public JSON2Object json2() throws SQLException {
        return (JSON2Object) JSON2.valueOf(map());
    }
    
    @Override
    public E obj() throws SQLException {
      return (E) json2().typeMap(table.def);
    }
    
    /**
     * Eとtypeが一致しないとあれなので仮
     * @param type
     * @return
     * @throws SQLException 
     */
    @Override
    public E typeMap(Type type) throws SQLException {
        return (E) json2().typeMap(type);
    }
}
