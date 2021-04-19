package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.Index;
import net.siisise.d3bif.Sequence;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.base.CacheSchema;

/**
 * JDBCによるSchemaっぽい処理まとめ
 */
public class RemoteSchema extends CacheSchema {

    protected RemoteSchema(RemoteCatalog db, String name) {
        super(db, name);
    }

    @Override
    public String escFullName() {
        if (database.getName() == null) {
            return escName();
        } else {
            return database.escName() + "." + escName();
        }
    }

    @Override
    public RemoteTable newTable(String name) {
        return new RemoteTable(this, name);
    }
    
    @Override
    public RemoteTable newTable(Class cls) {
        return new RemoteTable(this,cls);
    }
    
    @Override
    public RemoteIndex newIndex(String name) {
        return new RemoteIndex(this,name);
    }

    /**
     * 実体とは紐付かないTable
     *
     * @param name
     * @return RefTableかRemoteTableかは未定
     * @throws java.sql.SQLException
     */
    @Override
    public RemoteTable dbTable(String name) throws SQLException {
        Connection con = getConnection();
        try {
            RemoteTable table = newTable(name);
            tableMap.put(name, table);
            table.columns(con.getMetaData());
            return table;
        } finally {
            release(con);
        }
    }
    
    public RemoteTable dbTable(Class cls) throws SQLException {
        Connection con = getConnection();
        try {
            String tableName = cls.getCanonicalName().toLowerCase();
            RemoteTable table = newTable(cls);
            tableMap.put(tableName, table);
            table.columns(con.getMetaData());
            return table;
        } finally {
            release(con);
        }
    }

    /**
     * 
     * @param name
     * @return
     * @throws SQLException 
     */
    @Override
    public RemoteSequence dbSequence(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Index dbIndex(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * table, view, sequenceの一覧
     * indexは使い道があれば取得する
     * @param con
     * @throws SQLException 
     */
    void dbTables(Connection con) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet tableResult = meta.getTables(database.getName(), name, "%", new String[]{"TABLE","VIEW","SEQUENCE"});
        // TABLE_CAT TABLE_SCHEM TABLE_NAME TABLE_TYPE REMARKS TYPE_CAT TYPE_SCHEM TYPE_NAME
        Map<String, Table> tables = new HashMap<>();
        Map<String, Index> indexes = new HashMap<>();
        Map<String, Sequence> sequences = new HashMap<>();
        String tableName;
        String tableType;
        while (tableResult.next()) {
            tableName = tableResult.getString("TABLE_NAME");
            tableType = tableResult.getString("TABLE_TYPE");
            System.out.println("TABLE_NAME:" + tableName + " TABLE_TYPE:" + tableType );
            if ( "table".equalsIgnoreCase(tableType)) {
                RemoteTable table = newTable(tableName);
                table.columns(meta);
                tables.put(tableName, table);
            } else if ("index".equalsIgnoreCase(tableType)) {
                Index index = newIndex(tableName);
                indexes.put(tableName, index);
            } else if ( "sequence".equalsIgnoreCase(tableType)) {
                Sequence sequence = newSequence(tableName);
                sequences.put(tableName, sequence);
            }
        }
        tableMap = tables;
        indexMap = indexes;
        sequenceMap = sequences;
    }

    @Override
    public List<RemoteTable> dbTables() throws SQLException {
        Connection con = getConnection();
        try {
            dbTables(con);
        } finally {
            release(con);
        }
        List<RemoteTable> tables = new ArrayList<>();
        for ( Table obj : tableMap.values() ) {
            tables.add((RemoteTable) obj);
        }
        return tables;
    }

    @Override
    public List<RemoteIndex> dbIndexes() throws SQLException {
        Connection con = getConnection();
        try {
            dbTables(con);
        } finally {
            release(con);
        }
        List<RemoteIndex> indexes = new ArrayList<>();
        indexes.addAll((List)indexMap.values());
//        for ( Index obj : indexMap.values() ) {
//            indexes.add((RemoteIndex) obj);
//        }
        return indexes;
    }

    @Override
    public List<RemoteSequence> dbSequences() throws SQLException {
        Connection con = getConnection();
        try {
            dbTables(con);
        } finally {
            release(con);
        }
        List<RemoteSequence> tables = new ArrayList<>();
        tables.addAll((List)sequenceMap.values());
//        sequenceMap.values().forEach(sequence -> {
//            tables.add((RemoteSequence) sequence);
//        });
        return tables;
    }
    
    protected Connection getConnection() throws SQLException {
        return ((RemoteCatalog) database).getConnection();
    }

    protected void release(Connection con) throws SQLException {
        ((RemoteCatalog) database).release(con);
    }

    RemotePreUpdate getPreUpdate() throws SQLException {
        return ((RemoteCatalog) database).getPreUpdate();
    }
}
