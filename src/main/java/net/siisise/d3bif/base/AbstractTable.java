package net.siisise.d3bif.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.BaseTable;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.Schema;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.where.Condition;
import net.siisise.json.JSONObject;
import net.siisise.json.JSONValue;

/**
 * 実装の元
 * Map系の実装でObject系、JSON系も使える
 *
 * @param <E> マッピング対応型
 */
public abstract class AbstractTable<E> extends AbstractBaseTable<E> implements Table<E> {

    protected AbstractTable(Schema schema, String name, Column... columns) {
        super(schema, name);
    }
    
    protected AbstractTable(Schema schema, Class<E> cls) {
        super(schema, cls);
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
        insert(json(obj));
    }

    JSONObject json(E obj) throws SQLException {
        JSONValue json = JSONValue.valueOf(obj);
        if (json instanceof JSONObject) {
            return (JSONObject) json;
        } else {
            throw new SQLException();
        }
    }

    @Override
    public void insert(JSONObject json) throws SQLException {
        System.out.println(json.toString());
        insert(json.map());
    }

    @Override
    public void insert(Map<String, Object> values) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
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
    public void remove(Condition conditions) throws SQLException {
        schema.sql("DELETE", "FROM", escFullName(), where(conditions));
    }

    /**
     * 取得時は外部参照込み
     * @param rs
     * @return
     * @throws SQLException 
     */
    @Override
    public JSONObject json(ResultSet rs) throws SQLException {
        return (JSONObject) JSONValue.valueOf(map(rs));
    }
    
    public List<JSONObject> json(Condition condition) throws SQLException {
        ResultSet rs = query(condition);
        List<JSONObject> results = new ArrayList<>();
        while ( rs.next() ) {
            results.add(json(rs));
        }
        return results;
    }

    @Override
    public E obj(ResultSet rs) throws SQLException {
        return json(rs).map(def);
    }


    public E obj(JSONObject json) throws SQLException {
        return json.map(def);
    }

    public List<E> obj(Condition condition) throws SQLException {
        ResultSet rs = query(condition);
        List<E> results = new ArrayList<>();
        while ( rs.next() ) {
            results.add(obj(rs));
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
            if ( col.isExportedKey() ) {
                Column exRefCol = col.exportedColumn();
                BaseTable exRefTbl = exRefCol.getTable();
                Table exTbl;
                if (getName().equals(exRefTbl.getName())) {
                    exTbl = this;
                } else {
                    exTbl = getSchema().dbTable(exRefTbl.getName());
                }
                Column exCol = exTbl.col(exRefCol.getName());
                // ToDo: キーがnot null ならnullのとき省略したい ?
                Condition cnd = Condition.EQ(exCol, (String)map.get(columnName)); // intもある
                ResultSet exRs = exTbl.query(cnd);
                while ( exRs.next() ) {
                    map.put(columnName, exTbl.map(exRs));
                }
            } else {
            }
        }
        return map;
    }
    
    public List<Map<String,Object>> map(Condition condition ) throws SQLException {
        ResultSet rs = query(condition);
        List<Map<String,Object>> results = new ArrayList<>();
        while ( rs.next() ) {
            results.add(map(rs));
        }
        return results;
    }
}
