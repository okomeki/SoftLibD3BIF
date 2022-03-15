package net.siisise.d3bif.base;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import net.siisise.d3bif.Schema;
import net.siisise.d3bif.Catalog;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.BaseTable;
import net.siisise.d3bif.Index;
import net.siisise.d3bif.Sequence;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.ref.RefSchema;
import net.siisise.json.JSONObject;

/**
 *
 */
public abstract class AbstractSchema implements Schema {
    protected final Catalog database;
    protected String name;
    
    protected AbstractSchema(Catalog db, String name) {
        database = db;
        this.name = name;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Catalog getCatalog() {
        return database;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String escName() {
        return "\"" + name + "\"";
    }
    
    /**
     * 実装によって異なるのでPostgreSQLっぽい例
     * @return 
     */
    @Override
    public String escFullName() {
        if ( database == null) {
            return escName();
        }
        return database.escName() + "." + escName();
    }
    
    @Override
    public void sql(String cmd, String... options) throws SQLException {
        database.sql(cmd, options);
    }
    
    @Override
    public String preSQL(String cmd, String... options) {
        return database.preSQL(cmd, options);
    }
    
    @Override
    public Table newTable(BaseTable srcTable) throws SQLException {
        Table table = newTable(srcTable.getName());
        table.copy(srcTable);
        return table;
    }
    
    @Override
    public Table cacheTable(String name) throws SQLException {
        return dbTable(name);
    }

    /**
     *
     * @param table
     * @param options
     * @return
     * @throws SQLException
     */
    @Override
    public Table createTable(BaseTable table, String... options) throws SQLException {
        Table xTable = newTable(table);
        Collection<Column> columns = table.columns();
        StringBuilder sb = new StringBuilder();
        
        char pre = '(';
        for ( Column column : columns ) {
            sb.append(pre);
            pre = ',';
            sb.append(column.escName());
            for ( String type : column.getTypes()) {
                sb.append(" ");
                sb.append(type);
            }
//            sb.append(column.getType());
        }
        sb.append(")");
        sql("CREATE TABLE " + xTable.escFullName() + " " + sb.toString(), options);
        return dbTable(table.getName());
    }
    
    @Override
    public Table createTable(Class struct, String... options) throws SQLException {
        return createTable(RefSchema.defineOf(struct),options);
    }
    
    @Override
    public Table createTable(String name, Map<String,Object> struct, String... options) throws SQLException {
        return createTable(RefSchema.defineOf(name, struct),options);
    }
    
    @Override
    public Table createTable(String name, JSONObject obj, String... options) throws SQLException {
        return createTable(RefSchema.defineOf(name, obj.map()));
    }

    @Override
    public void drop(BaseTable table) throws SQLException {
        sql("DROP TABLE", newTable(table).escFullName());
    }
    
    @Override
    public Index cacheIndex(String name) throws SQLException {
        return dbIndex(name);
    }
    
    @Override
    public void drop(Index index) throws SQLException {
        sql("DROP INDEX", newIndex(index.getName()).escFullName());
    }
    
    @Override
    public Sequence newSequence(String name) {
        return new AbstractSequence(this,name);
    }

    @Override
    public Sequence cacheSequence(String name) throws SQLException {
        return dbSequence(name);
    }

    @Override
    public Sequence createSequence(String name) throws SQLException {
        Sequence seq = newSequence(name);
        sql("CREATE SEQUENCE",seq.escFullName());
        return seq;
    }

    @Override
    public void drop(Sequence sequence) throws SQLException {
        sql("DROP SEQUENCE",sequence.escFullName());
    }
}
