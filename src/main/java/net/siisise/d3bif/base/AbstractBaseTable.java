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
 * 定義用っぽい
 * @param <E>
 */
public abstract class AbstractBaseTable<E> implements BaseTable {

    protected Schema schema;
    protected String name;

    /**
     * 型定義には使っていないかも
     * オブジェクト変換に使用する
     */
    Class<E> def;

    /**
     * Ref?
     */
    protected Map<String,Column> columns = new HashMap<>();
    protected List<Column> primaryKeys = new ArrayList<>();
    protected List<Column> uniqueKeys = new ArrayList<>();
    protected List<Column> importedKeys = new ArrayList<>();

    protected AbstractBaseTable(Schema schema, String name,Column... columns) {
        this.schema = schema;
        this.name = name;
        for ( Column col : columns ) {
            this.columns.put(col.getName(),col(col));
            
        }
    }

    /**
     * 
     * @param schema
     * @param cls 
     */
    protected AbstractBaseTable(Schema schema, Class<E> cls) {
        this(schema, cls.getSimpleName().toLowerCase());
        def = cls;
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
    public Class<E> getObjectClass() {
        return def;
    }
    
    @Override
    public void copy(BaseTable src) throws SQLException {
        def = src.getObjectClass();
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
        for ( Column srcKey : src.importedKeys() ) {
            Column key = col(srcKey);
            importedKeys.add(key);
        }
    }
    
    /**
     *
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public List<Column> primaryKeys() throws SQLException {
        return new ArrayList<>(primaryKeys);
    }

    @Override
    public List<Column> uniqueKeys() throws SQLException {
        return new ArrayList<>(uniqueKeys);
    }

    @Override
    public List<Column> importedKeys() throws SQLException {
        return new ArrayList<>(importedKeys);
    }
}
