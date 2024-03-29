package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.Catalog;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.Schema;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.base.AbstractColumn;
import net.siisise.d3bif.base.AbstractTable;
import net.siisise.d3bif.where.Condition;

/**
 * 構造情報はローカルでも持つ感じに仕上がっているかもしれない
 * @param <E> マッピング対応型
 */
public class RemoteTable<E> extends AbstractTable<E> {
    List<Column> cols;
    
    protected RemoteTable(RemoteSchema schema, String name) {
        super(schema,name);
    }

    protected RemoteTable(RemoteSchema schema, Class cls) {
        super(schema,cls);
    }

    @Override
    public RemoteColumn newColumn(String name) {
        return new RemoteColumn(this,name);
    }
    /**
     *
     * @param name
     * @return
     */
    @Override
    public Column col(String name) {
        Column col = columns.get(name);
        if ( col == null ) {
            col = newColumn(name);
            columns.put(name,col);
        }
        return col;
    }
    
    @Override
    public List<Column> columns() throws SQLException {
        if ( cols == null ) {
            Connection con = ((RemoteSchema)schema).getConnection();
            cols = columns(con.getMetaData());
            ((RemoteSchema)schema).release(con);
        }
        return cols;
    }
    
    /**
     *
     * @param meta
     * @return
     * @throws SQLException
     */
    public List<Column> columns(DatabaseMetaData meta) throws SQLException {
        ResultSet columnResult = meta.getColumns(schema.getCatalog().getName(), schema.getName(), getName(), "%");
        Map<String,Column> cols = new HashMap<>();
        List<Column> colList = new ArrayList<>();
        printResult(columnResult);
        
        while ( columnResult.next() ) {
            RemoteColumn column = (RemoteColumn) col(columnResult.getString("COLUMN_NAME"));
            column.setType(columnResult.getInt("DATA_TYPE"), columnResult.getString("TYPE_NAME"));
            
            cols.put(column.getName(),column);
            colList.add(column);
        }
        columns = cols;
        primaryKeys(meta); // 主キー
        importedKeys(meta); // 外部キー
        // unique がほしいか?
        return colList;
    }
    
    /**
     * 既存のColumnsに主キー情報をつけるだけ
     * @param meta
     * @return
     * @throws SQLException
     */
    public List<Column> primaryKeys(DatabaseMetaData meta) throws SQLException {
        ResultSet pkeyResult = meta.getPrimaryKeys(schema.getCatalog().getName(), schema.getName(), getName());
        List<Column> pKeys = new ArrayList<>();
        while ( pkeyResult.next() ) {
            Column column = col(pkeyResult.getString("COLUMN_NAME"));
            pKeys.add(column.PRIMARYKEY());
        }
        primaryKeys = pKeys;
        return pKeys;
    }
    
    private Schema schema( String schemaName ) throws SQLException {
        Catalog catalog = schema.getCatalog();
//            Catalog pkCatalog = ( pkCatalogName == null ) ? schema.getCatalog() : schema.getCatalog();
        Schema schema = ( schemaName == null ) ? catalog.getDefaultSchema() : catalog.schema(schemaName);
        return schema;
    }
    
    /**
     * 
     * @param schemaName
     * @param tableName
     * @return
     * @throws SQLException 
     */
    private Table table(String schemaName, String tableName ) throws SQLException {
        Schema schema = schema(schemaName);
        Table table;
        if ( !schema.getName().equals(this.schema.getName()) || !tableName.equals(getName())) { // 完全一致? ToDo: スキーマの一致
            table = schema.cacheTable(tableName); // るーぷする
        } else {
            table = this;
        }
        return table;
    }
    
    
    /**
     * 外部から参照されている?キー
     * catalogのないPostgreSQLにあわせて書いてある
     * @param meta
     * @return
     * @throws SQLException 
     */
    public List<Column> importedKeys(DatabaseMetaData meta) throws SQLException {
        ResultSet ikeyResult = meta.getImportedKeys(schema.getCatalog().getName(), schema.getName(), getName());
        
        List<Column> iKeys = new ArrayList<>();
        
        while ( ikeyResult.next() ) {
            // 参照される側
            String pkCatalogName = ikeyResult.getString("PKTABLE_CAT");
            String pkSchemaName = ikeyResult.getString("PKTABLE_SCHEM");
            String pkTableName = ikeyResult.getString("PKTABLE_NAME");
            String pkColumnName = ikeyResult.getString("PKCOLUMN_NAME");
            System.out.println("PK:"+pkCatalogName +"."+ pkSchemaName + "." + pkTableName +"."+ pkColumnName);

            // 参照する側
            String fkCatalogName = ikeyResult.getString("FKTABLE_CAT");
            String fkSchemaName = ikeyResult.getString("FKTABLE_SCHEM");
            String fkTableName = ikeyResult.getString("FKTABLE_NAME");
            String fkColumnName = ikeyResult.getString("FKCOLUMN_NAME");
            System.out.println("FK:"+fkCatalogName +"."+ fkSchemaName + "." + fkTableName +"."+ fkColumnName);

            Table pkTable;
            Column pkColumn;

            pkTable = table( pkSchemaName, pkTableName );
            pkColumn = (RemoteColumn) pkTable.col(pkColumnName);

            Table fkTable;
            Column fkColumn;
            
            fkTable = table( fkSchemaName, fkTableName );
            fkColumn = (RemoteColumn) fkTable.col(fkColumnName);
//            fKeys.add(fkColumn);
            fkColumn.REFERENCES(pkColumn);
            iKeys.add(fkColumn);
        }
        importedKeys = iKeys;
        return iKeys;
    }
    
    /**
     * 主キー以外でもuniqueっぽいのがあれば
     * @param meta
     * @param scope
     * @param nullable
     * @return
     * @throws SQLException 
     */
    public List<Column> getBestKeys(DatabaseMetaData meta, int scope, boolean nullable ) throws SQLException {
        ResultSet rs = meta.getBestRowIdentifier(schema.getCatalog().getName(), schema.getName(), getName(), scope, nullable);
        List<Column> bKeys = new ArrayList<>();
        while ( rs.next() ) {
            RemoteColumn column = (RemoteColumn) col(rs.getString("COLUMN_NAME"));
            bKeys.add(column);
        }
        return bKeys;
    }
    
    void printResult(ResultSet rs) throws SQLException {
        rs.beforeFirst();
        ResultSetMetaData meta = rs.getMetaData();
        int len = meta.getColumnCount();
        while ( rs.next() ) {
            for ( int i = 1; i <= len; i++) {
                System.out.print(meta.getColumnName(i) + ":" + rs.getString(i));
                System.out.print(",");
            }
            System.out.println();
        }
        
        rs.beforeFirst();
    }
    
    RemotePreUpdate getPreUpdate() throws SQLException {
        return ((RemoteSchema)schema).getPreUpdate();
    }
    
    /**
     * 
     * @param condition
     * @return
     * @throws SQLException 
     */
    @Override
    public ResultSet query(Condition condition) throws SQLException {
        RemotePreUpdate pu = getPreUpdate();
        String sql;
        sql = schema.preSQL("SELECT","*","FROM", escFullName(), where(condition) );
        pu.ps = pu.con.prepareStatement(sql);
        ResultSet rs = pu.ps.executeQuery();
        //printResult(rs);
        pu.close(); // spool に戻るだけでしばらく使える(予定)
        return rs;
    }

    @Override
    public int size() throws SQLException {
        return count(null);
    }
    
    @Override
    public int count(Condition condition) throws SQLException {
        String sql = schema.preSQL("SELECT", "COUNT(*)", "FROM", escFullName(), where(condition));
        RemotePreUpdate pu = getPreUpdate();
        Statement st = pu.con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int size = 0;
        while ( rs.next() ) {
            size = rs.getInt(1);
        }
        pu.close();
        return size;
    }
    
    void release(ResultSet rs) {
        throw new UnsupportedOperationException("まだ");
    }

    // insert用
    String sqlAndKeys(List<String> cols) {
        StringBuilder sb = new StringBuilder();

        for ( String col : cols ) {
            if ( sb.length() > 0 ) {
                sb.append(",");
            }
            sb.append(col);
        }
        return sb.toString();
    }

    /**
     * 一括
     * @param valueList
     * @throws SQLException 
     */
    @Override
    public void insert(List<Map<String,Object>> valueList) throws SQLException {
        List<String> options = new ArrayList<>();
        List<String> keys;

        options.add(escFullName());
        options.add("(");
        
        keys = new ArrayList(valueList.get(0).keySet());

        options.add(sqlAndKeys(keys));

        options.add(") VALUES(");
        int len = keys.size();
        for ( int i = 1; i < len; i++ ) {
            options.add("?,");
        }
        options.add("? )");
        
        String sql = schema.preSQL("INSERT INTO", options.toArray(new String[0]));
        RemotePreUpdate pu = getPreUpdate();
        System.out.println(sql);
        pu.ps = pu.con.prepareStatement(sql);
        for ( Map<String,Object> map : valueList ) {
            setList(pu.ps, keys, map);
            pu.ps.executeUpdate();
        }
        
        pu.close();
    }
    
    /**
     * 
     * @param ps
     * @param colNames
     * @param obj
     * @throws SQLException 
     */
    void setList(PreparedStatement ps, List<String> colNames, Map<String,Object> obj) throws SQLException {
        int i = 1;
        for ( String colName : colNames ) {
            conv(ps, i++, col(colName), obj.get(colName));
        }
    }
    
    void conv(PreparedStatement ps, int i, Column col, Object val) throws SQLException {
        if (col.isImportedKey() && val != null) {
            Column pk = ((AbstractColumn)col).importedColumn();
            val = ((Map<String,Object>)val).get(pk.getName());
        }
        switch (col.getType()) {
                case Types.VARCHAR:
                    ps.setString(i, (String)val);
                    break;
                case Types.INTEGER:
                    ps.setInt(i, (int)val);
                    break;
//                case Types.DOUBLE:
//                    ps.setDouble(i, (double)val);
//                    break;
//                case Types.TIMESTAMP_WITH_TIMEZONE:
//                    ps.setTimestamp(i, (Timestamp)val);
//                    break;
//                case Types.BOOLEAN:
//                    ps.setBoolean(i, (boolean)val);
//                    break;
                default:
                    System.out.println(col.getType());
                    throw new SQLException("未知の型 " + col.getName() + ":"+ col.getType());
        }
    }
    
    @Override
    public void update(Map<String,Object> map, Condition conditions) throws SQLException {
        List<String> colNames = new ArrayList(map.keySet());
        RemotePreUpdate pre = preUpdate(colNames, conditions);
        setList(pre.ps,colNames,map);
        pre.ps.executeUpdate();
        pre.close();
    }

    @Override
    public RemotePreUpdate update(Condition conditions) throws SQLException {
        List<String> colNames = new ArrayList<>();
        List<Column> cols = columns();
        for ( Column col : cols ) {
            colNames.add(col.getName());
        }
        return preUpdate(colNames, conditions);
    }
    
    public RemotePreUpdate preUpdate(List<String> cols, Condition conditions) throws SQLException {
        List<String> params = new ArrayList<>();
        params.add(escFullName());
        params.add("SET");
        
        for (String col : cols ) {
            if (params.size() > 2) {
                params.add(",");
            }
            params.add(col);
            params.add("=?");
        }

        // FROM FROMリスト
        
        // WHERE 条件
        params.add(where(conditions));
        
        String sql = schema.preSQL("UPDATE", params.toArray(new String[0]));
        RemotePreUpdate pre = getPreUpdate();
        pre.ps = pre.con.prepareStatement(sql);
        System.out.println(sql);
        
        return pre;
    }
}
