package net.siisise.d3bif.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.BaseTable;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.Schema;

/**
 *
 * @author okome
 */
public abstract class AbstractBaseTable implements BaseTable {
    protected Schema schema;
    protected String name;
    /**
     * Ref?
     */
    protected Map<String,Column> columns = new HashMap<>();
    protected List<Column> primaryKeys = new ArrayList<>();
    protected List<Column> uniqueKeys = new ArrayList<>();
    protected List<Column> exportedKeys = new ArrayList<>();
    
    protected AbstractBaseTable(Schema schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * ToDo: 仮 まだてきとー
     * @return 
     */
    @Override
    public String escName() {
        return "\"" + name + "\"";
    }

    /**
     * JDBCなら接続子は参照できるはず
     * @return 
     */
    @Override
    public String escFullName() {
        if ( schema == null || schema.getName() == null ) {
            return escName();
        }
        return schema.escName() + "." + escName();
    }

    @Override
    public Column col(String name) {
        return columns.get(name);
    }
    
    
    @Override
    public Column col(Column srcColumn) {
        Column col = col(srcColumn.getName());
        col.copy(srcColumn);
        // なにか
        return col;
    }
    
    @Override
    public void copy(BaseTable src) throws SQLException {
        for ( Column srcCol : src.columns() ) {
            Column col = col(srcCol);
            columns.put(col.getName(),col);
        }
        for ( Column srcKey : src.primaryKeys() ) {
            Column key = col(srcKey);
            primaryKeys.add(key);
        }
        for ( Column srcKey : src.uniqueKeys() ) {
            Column key = col(srcKey);
            uniqueKeys.add(key);
        }
        for ( Column srcKey : src.exportedKeys() ) {
            Column key = col(srcKey);
            exportedKeys.add(key);
        }
    }
    
    /**
     *
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public List<Column> primaryKeys() throws SQLException {
        return primaryKeys;
    }

    @Override
    public List<Column> uniqueKeys() throws SQLException {
        return uniqueKeys;
    }

    @Override
    public List<Column> exportedKeys() throws SQLException {
        return exportedKeys;
    }
}
