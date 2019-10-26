package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.siisise.d3bif.base.AbstractSchema;
import net.siisise.d3bif.Table;

/**
 * JDBCによるSchemaっぽい処理まとめ
 */
public class RemoteSchema extends AbstractSchema {

    protected Map<String, RemoteTable> tableMap = new HashMap<>();

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

    /**
     *
     * @param name
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public RemoteTable cacheTable(String name) throws SQLException {
        RemoteTable table = tableMap.get(name);
        if (table == null) {
            table = dbTable(name);
            //tableMap.put(name, table);
        }
        return table;
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

        if (tableMap == null) {
            tableMap = new HashMap<>();
        }
        //
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
        if (tableMap == null) {
            tableMap = new HashMap<>();
        }
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

    void dbTables(Connection con) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet tableResult = meta.getTables(database.getName(), name, "%", new String[]{"TABLE"});
        Map<String, RemoteTable> tables = new HashMap<>();
        String tableName;
        while (tableResult.next()) {
            tableName = tableResult.getString("TABLE_NAME");
            RemoteTable table = newTable(tableName);
            table.columns(meta);
            tables.put(tableName, table);
        }
        tableMap = tables;
    }

    @Override
    public Collection<RemoteTable> tables() throws SQLException {
        if (tableMap == null) {
            Connection con = getConnection(); // = database.create(this);
            try {
                dbTables(con);
            } finally {
                release(con);
            }
        }
        return tableMap.values();
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
