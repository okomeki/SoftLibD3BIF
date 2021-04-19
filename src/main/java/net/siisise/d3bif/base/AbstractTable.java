package net.siisise.d3bif.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import net.siisise.d3bif.BaseTable;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.MapTable;
import net.siisise.d3bif.Schema;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.where.Condition;
import net.siisise.json.JSON;
import net.siisise.json.JSONObject;
import net.siisise.json.JSONValue;
import net.siisise.d3bif.MapResultSet;
import net.siisise.json2.JSON2;
import net.siisise.json2.JSON2Array;
import net.siisise.json2.JSON2Object;
import net.siisise.json2.JSON2Value;

/**
 * 実装の元 Map系の実装でObject系、JSON系も使える
 *
 * @param <E> マッピング対応型
 */
public abstract class AbstractTable<E> extends AbstractBaseTable<E> implements Table<E>,MapTable {

    protected AbstractTable(Schema schema, String name, Column... columns) {
        super(schema, name);
    }

    protected AbstractTable(Schema schema, Class<E> cls) {
        super(schema, cls);
    }

    @Override
    public void drop() throws SQLException {
        schema.drop(this);
    }

    /**
     * 簡単な変換
     *
     * @param obj
     * @return
     */
    @Override
    public Condition key(E obj) throws SQLException {
        return key(json(obj));
    }

    @Override
    public Condition key(JSONObject json) throws SQLException {
        return key(json.map());
    }

    /**
     *
     * @param obj
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public Condition key(Map<String, Object> obj) throws SQLException {
        List<Condition> cnds = new ArrayList();
        List<Column> pkeys = primaryKeys();
        for (Column key : pkeys) {
            cnds.add(Condition.EQ(key, obj.get(key.getName()).toString()));
        }
        return Condition.AND(cnds.toArray(new Condition[0]));
    }

    protected String where(Condition conditions) {
        if (conditions != null) {
            return "WHERE " + conditions.toString();
        }
        return "";
    }

    @Override
    public void insert(E obj) throws SQLException {
        insert(json2(obj));
    }

    JSONObject json(E obj) throws SQLException {
        JSONValue json = JSON.valueOf(obj);
        if (json instanceof JSONObject) {
            return (JSONObject) json;
        } else {
            throw new SQLException();
        }
    }

    /**
     * 不要かもしれない
     * @param obj
     * @return
     * @throws SQLException 
     */
    JSON2Object json2(E obj) throws SQLException {
        JSON2Value json = JSON2.valueOf(obj);
        if (json instanceof JSON2Object) {
            return (JSON2Object) json;
        } else {
            throw new SQLException();
        }
    }

    @Override
    public void insert(JSONObject json) throws SQLException {
        //System.out.println(json.toString());
        insert(json.map());
    }

    @Override
    public void insert(Map<String, Object> values) throws SQLException {
        List<Map<String, Object>> list = new JSON2Array();
        list.add(values);
        insert(list);
    }

    public abstract void insert(List<Map<String, Object>> list) throws SQLException;

    @Override
    public void update(Map<String, Object> map) throws SQLException {
        update(map, key(map));
    }

    @Override
    public void update(E obj, Condition con) throws SQLException {
        update(json(obj), con);
    }

    @Override
    public void update(JSONObject json, Condition con) throws SQLException {
        update(json.map(), con);
    }

    @Override
    public void update(E obj) throws SQLException {
        update(json(obj));
    }

    @Override
    public void update(JSONObject json) throws SQLException {
        update(json.map());
    }

    @Override
    public ResultSet query() throws SQLException {
        return query(null);
    }
    
    @Override
    public MapResultSet queryMap(Condition condition) throws SQLException {
        return new AbstractMapResultSet(this, condition);
    }

    @Override
    public MapResultSet queryMap() throws SQLException {
        return new AbstractMapResultSet(this);
    }

    @Override
    public void remove(Condition conditions) throws SQLException {
        schema.sql("DELETE", "FROM", escFullName(), where(conditions));
    }

    /**
     * 取得時は外部参照込み
     * JSON Parserは走らない
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    @Override
    public JSONObject json(ResultSet rs) throws SQLException {
        return (JSONObject) JSON.valueOf(map(rs));
    }

    @Override
    public JSON2Object json2(ResultSet rs) throws SQLException {
        return (JSON2Object) JSON2.valueOf(map(rs));
    }

    @Override
    public List<JSONObject> json(Condition condition) throws SQLException {
        MapResultSet rs = AbstractTable.this.queryMap(condition);
        List<JSONObject> results = new ArrayList<>();
        while (rs.next()) {
            results.add(rs.json());
        }
        return results;
    }

    @Override
    public List<JSON2Object> json2(Condition condition) throws SQLException {
        MapResultSet rs = AbstractTable.this.queryMap(condition);
        List<JSON2Object> results = new ArrayList<>();
        while (rs.next()) {
            results.add(rs.json2());
        }
        return results;
    }

    @Override
    public List<JsonObject> toJson(Condition condition) throws SQLException {
        MapResultSet rs = queryMap(condition);
        List<JsonObject> jret = new ArrayList<>();
        while ( rs.next() ) {
            jret.add(rs.json2().toJson());
        }
        return jret;
    }

    /**
     * JSON経由しなくてもいい
     * @param rs
     * @return
     * @throws SQLException 
     */
    @Override
    public E obj(ResultSet rs) throws SQLException {
        return (E) json2(rs).typeMap(def);
    }

    @Override
    public E obj(JSONObject json) throws SQLException {
        return json.typeMap(def);
    }

    @Override
    public E obj(JSON2Object json) throws SQLException {
        return (E) json.typeMap(def);
    }

    @Override
    public List<E> obj(Condition condition) throws SQLException {
        MapResultSet<E> rs = AbstractTable.this.queryMap(condition);
        List<E> results = new ArrayList<>();
        while (rs.next()) {
            results.add(rs.obj());
        }
        return results;
    }

    /**
     *
     * @see
     * net.siisise.d3bif.remote.RemoteTable#setList(java.sql.PreparedStatement,
     * java.util.List, java.util.Map)
     * @param rs
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Object> map(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        for (String columnName : columns.keySet()) {
            Column col = columns.get(columnName);
            switch (col.getType()) {
                case Types.INTEGER:
                    map.put(columnName, rs.getInt(columnName));
                    break;
                case Types.VARCHAR:
                    map.put(columnName, rs.getString(columnName));
                    break;
                default:
                    throw new IllegalStateException("まだない:" + col.getType());
            }
            if (col.isImportedKey()) {
                Column exRefCol = col.importedColumn();
                BaseTable exRefTbl = exRefCol.getTable();
                Table exTbl;
                if (getName().equals(exRefTbl.getName())) {
                    exTbl = this;
                } else {
                    exTbl = getSchema().cacheTable(exRefTbl.getName());
                }
                Column exCol = exTbl.col(exRefCol.getName());
                // ToDo: キーがnot null ならnullのとき省略したい ?
                Condition cnd = Condition.EQ(exCol, (String) map.get(columnName)); // intもある
                List<Map<String,Object>> exr = exTbl.map(cnd);
                MapResultSet exRs = exTbl.queryMap(cnd);
                while (exRs.next()) {
                    map.put(columnName, exRs.map());
                }
            } else {
            }
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> map(Condition condition) throws SQLException {
        MapResultSet rs = AbstractTable.this.queryMap(condition);
        List<Map<String, Object>> results = new ArrayList<>();
        while (rs.next()) {
            results.add(rs.map());
        }
        return results;
    }
}
