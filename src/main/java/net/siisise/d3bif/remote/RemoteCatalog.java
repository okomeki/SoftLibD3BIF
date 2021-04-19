package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.siisise.d3bif.base.AbstractCatalog;

/**
 * JDBCのcatalog相当なので名前変える?
 * Connection Manager を中で持つか外で持つか
 */
public class RemoteCatalog extends AbstractCatalog {
    String jdbcURL;
    protected String user;
    protected String pass;
    
    /**
     * とりあえずそんなに使わない
     */
    final int CONNECTIONPOOL_MAX = 5;
    /**
     * 弱参照のものに変えてみてもいい
     */
    private final List<Connection> connectionPool = new ArrayList<>();
    
    protected RemoteCatalog(String catalogName, String jdbc, String user, String pass) {
        super(catalogName);
        jdbcURL = jdbc;
        this.user = user;
        this.pass = pass;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * ここは分離可能にしたい
     * spoolがある場合はspoolから返すが、spoolが空でも上限はない
     * @return
     * @throws SQLException 
     */
    public synchronized Connection getConnection() throws SQLException {
        if ( !connectionPool.isEmpty() ) { // 要:同期
            Connection conn = connectionPool.get(0);
            connectionPool.remove(0);
            return conn;
        }
        return DriverManager.getConnection(jdbcURL, user, pass);
    }
    
    protected RemotePreUpdate getPreUpdate() throws SQLException {
        return new RemotePreUpdate(this, getConnection());
    }
    
    public void release(Connection con) throws SQLException {
        if ( connectionPool.size() >= CONNECTIONPOOL_MAX ) { // 古いものから閉じる
            getConnection().close();
        }
        connectionPool.add(con);
    }
    
    /**
     * spool含めて閉じる
     * 利用中のものは閉じない
     * @throws SQLException 
     */
    public synchronized void close() throws SQLException {
        while ( !connectionPool.isEmpty() ) {
            Connection con = connectionPool.get(0);
            connectionPool.remove(con);
            con.close();
        }
    }
    
    /**
     * 最後の手段として残しておく
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } catch (SQLException ex) {
            Logger.getLogger(RemoteCatalog.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.finalize();
        
    }
    
    @Override
    public void sql(String cmd, String... add) throws SQLException {
        String sql = preSQL(cmd,add);
        Connection con = getConnection();
        try {
            Statement st = con.createStatement();
            System.out.println(sql);
            st.execute(sql);
//        PreparedStatement pst = con.prepareStatement(sql);
//        pst.execute();
            st.close();
        } finally {
            release(con);
        }
    }
    
    protected RemoteSchema newSchema(String name) {
        return new RemoteSchema(this,name);
    }

    @Override
    public RemoteSchema schema(String name) throws SQLException {
        RemoteSchema schema = (RemoteSchema) schemas.get(name);
        if ( schema == null ) { // なくても
            Connection con = getConnection();
            try {
                DatabaseMetaData meta = con.getMetaData();
                meta.getCatalogs();
                ResultSet rs = meta.getSchemas(getName(), name);
                while ( rs.next() ) {
                    schema = newSchema(rs.getString("TABLE_SCHEM"));
                    schemas.put(name,schema);
                }
            } finally {
                release(con);
            }
        }
        return schema;
    }

    @Override
    public RemoteSchema getDefaultSchema() throws SQLException {
        String currentName;
        Connection con = getConnection();
        try {
            currentName = con.getSchema();
        } finally {
            release(con);
        }
        return schema(currentName);
    }
    
    /**
     * 
     * @param meta
     * @return TABLE, VIEW, SYSTEM TABLE, GLOBAL TEMPORARY, LOCAL TEMPORARY, ALIAS, SYNONYM らしい
     * @throws SQLException 
     */
    List<String> getTableTypes(DatabaseMetaData meta) throws SQLException {
        List<String> types = new ArrayList<>();
        ResultSet rs = meta.getTableTypes();
        while ( rs.next() ) {
            types.add(rs.getString("TABLE_TYPE"));
        }
        return types;
    }
    
    public void typeInfo() throws SQLException {
        Connection con = getConnection();
        try {
            DatabaseMetaData meta = con.getMetaData();
            List<String> types = getTableTypes(meta);
            types.forEach(t -> {
                System.out.println("TABLE TYPE : " + t);
            });
            
            ResultSet rs = meta.getTypeInfo();
            while ( rs.next() ) {
                System.out.println("TYPE NAME : " + rs.getString("TYPE_NAME"));
                System.out.println("DATA TYPE : " + rs.getInt("DATA_TYPE"));
                System.out.println("NULLABLE  : " + rs.getShort("NULLABLE"));
            }
        } finally {
            release(con);
        }
    }
    
    
}
