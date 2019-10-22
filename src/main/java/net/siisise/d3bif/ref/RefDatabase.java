package net.siisise.d3bif.ref;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.Schema;
import net.siisise.d3bif.base.AbstractCatalog;

/**
 * 定義用
 * PostgreSQLのDATABASE相当品
 * @author okome
 */
public class RefDatabase extends AbstractCatalog {
    
    List<String> sqlPool = new ArrayList<>();

    /**
     * Ref以外が混在することもあるかもしれない(謎
     */
    Map<String,Schema> schemas = new HashMap<>();

    public RefDatabase(String name) {
        super(name);
    }

    @Override
    public Schema schema(String name) {
        Schema schema = schemas.get(name);
        if ( schema == null ) {
            schema = new RefSchema(this,name);
            schemas.put(name, schema);
        }
        return schema;
    }

    /**
     * abstractか何か作るか未定
     * @param cmd
     * @throws SQLException 
     */
    @Override
    public void sql(String cmd, String... options) throws SQLException {
        sqlPool.add(preSQL(cmd,options));
    }
    
    
    /**
     * どうにかする?
     * @return 
     */
    @Override
    public RefSchema getDefaultSchema() {
        return (RefSchema) schema(null);
    }
    
}
