package net.siisise.d3bif.pgsql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.siisise.d3bif.Catalog;
import net.siisise.d3bif.remote.RemoteCatalog;

/**
 * JDBC SQL99? catalog 相当
 */
public class PgDatabase extends RemoteCatalog {

    /** ポート含む */
    private final String server;

    /**
     * 違う形にするかもしれない
     * @param server
     * @param database JDBCのcatalogではないかもしれない
     * @param user
     * @param pass 
     */
    public PgDatabase(String server, String database, String user, String pass) {
        super(database, "jdbc:postgresql://" + server + "/" + database, user, pass);
        this.server = server;
    }
    
    /**
     * 横のCatalogに切り換え
     * @param catalog
     * @return 
     */
    public PgDatabase catalog(String catalog) {
        return new PgDatabase(server,catalog,user,pass);
    }
    
    /**
     * 
     * @param newCatalogName
     * @param enc
     * @param c1
     * @param c2
     * @return
     * @throws SQLException 
     */
    PgDatabase createDB(String newCatalogName, String enc, String owner, String c1, String c2) throws SQLException {
        PgDatabase pgDatabase = catalog(newCatalogName);
        List<String> params = new ArrayList<>();
        if ( owner != null ) {
            params.add("OWNER");
            params.add(owner);
        }
        if ( enc != null ) {
            params.add("ENCODING");
            params.add(enc);
        }
        if ( c1 != null || c2 != null) {
            params.add("TEMPLATE");
            params.add("template0");
        }
        if ( c1 != null ) {
            params.add("LC_COLLATE");
            params.add(escValue(c1));
        }
        if ( c2 != null ) {
            params.add("LC_CTYPE");
            params.add(escValue(c2));
        }
        
        return create(pgDatabase, params.toArray(new String[0]));
    }

    @Override
    public PgSchema schema(String name) {
        PgSchema schema = (PgSchema) schemas.get(name);
        if (schema == null ) {
            schema = new PgSchema(this,name);
            schemas.put(name, schema);
        }
        return schema;
    }
    
    /**
     * PostgreSQL専用?
     * 上がないのでここに作る
     * @param catalog
     * @param options
     * @return
     * @throws SQLException 
     */
    public PgDatabase create(Catalog catalog, String... options) throws SQLException {
        sql("CREATE DATABASE " + catalog.getName(), options);
        return catalog(catalog.getName());
    }
    
}
