package net.siisise.d3bif;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;
import net.siisise.json.JSONObject;

/**
 * ResultSetから拡張していかないといろいろ
 * 
 * @param <E>
 */
public interface MapResultSet<E> {
    
//    ResultSet getResultSet();

    boolean next() throws SQLException;
    
    Map<String,Object> map() throws SQLException;
    JSONObject json() throws SQLException;
    E typeMap(Type type) throws SQLException;
    E obj() throws SQLException;
}
