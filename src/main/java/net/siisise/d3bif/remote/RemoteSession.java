package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 単純な接続 使わないかも
 */
public class RemoteSession {
    private RemoteCatalog cat;
    Connection con;
    
    RemoteSession(RemoteCatalog cat, Connection connection) {
        this.cat = cat;
        con = connection;
    }
    
    DatabaseMetaData getMetaData() throws SQLException {
        return con.getMetaData();
    }
    
    void close() throws SQLException {
        cat.release(con);
        con = null;
    }
}
