package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.DriverManager;
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
 * @author okome
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
    
    public String getName() {
        return name;
    }
    
    /**
     * ここは分離可能にしたい
     * spoolがある場合はspoolから返すが、spoolが空でも上限はない
     * @return
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException {
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
    public void close() throws SQLException {
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
        Statement st = con.createStatement();
        System.out.println(sql);
        st.execute(sql);
        st.close();

//        PreparedStatement pst = con.prepareStatement(sql);
//        pst.execute();
        release(con);
    }

    @Override
    public RemoteSchema schema(String name) {
        RemoteSchema schema = (RemoteSchema) schemas.get(name);
        if ( schema == null ) { // なくても
            schema = new RemoteSchema(this,name);
            schemas.put(name,schema);
        }
        return schema;
    }

    @Override
    public RemoteSchema getDefaultSchema() throws SQLException {
        Connection con = getConnection();
        String currentName = con.getSchema();
        release(con);
        return schema(currentName);
    }
    
    
}
