package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.json.JsonObject;
import net.siisise.d3bif.where.Condition;
import net.siisise.json2.JSON2Object;

/**
 * key / insert / update はMap<String,Object> と同じなので略.
 */
public interface JSON2Table extends MapTable {
    
    JSON2Object json2(ResultSet rs) throws SQLException;
    List<JSON2Object> json2(Condition condition) throws SQLException;
    List<JsonObject> toJson(Condition condition) throws SQLException;

    MapResultSet queryMap(Condition condition) throws SQLException;
    MapResultSet queryMap() throws SQLException;

}
