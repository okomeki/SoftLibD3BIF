package net.siisise.d3bif.ref;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.PreUpdate;
import net.siisise.d3bif.base.AbstractTable;
import net.siisise.d3bif.where.Condition;

/**
 * 定義用
 * DefTable的な扱いなのでデータ行はできるだけ持たない
 * 
 * @author okome
 * @param <E>
 */
public class RefTable<E> extends AbstractTable<E> {
    
    RefTable(RefSchema schema, String name, Column... columns) {
        super(schema,name);
        for ( Column column : columns ) {
            ((RefColumn)column).setTable(this);
            add(column);
        }
    }
    
    public RefTable(String name, Column... columns) {
        this(null,name, columns);
    }
    
    RefTable(RefSchema schema, Class<E> cls) {
        super(schema,cls);
    }

    /**
     * 未定
     * @param column 
     */
    void add(Column column) {
        columns.put(column.getName(), column);
        if ( column.isPrimaryKey()) {
            primaryKeys.add(column);
        }
        if ( column.isExportedKey()) {
            exportedKeys.add(column);
        }
        // uniqueは別途
    }

    @Override
    public RefColumn col(String name) {
        RefColumn col = (RefColumn)columns.get(name);
        if ( col == null ) {
            col = new RefColumn(this,name);
            columns.put(name, col);
        }
        return col;
    }

    @Override
    public Collection<Column> columns() throws SQLException {
        return new ArrayList<>(columns.values());
    }

    @Override
    public ResultSet query(Condition condition) throws SQLException {
        schema.sql("SELECT * FROM", escFullName(), where(condition));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null; // 結果は返せない
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int count(Condition condition) throws SQLException {
        return 0;
    }

    @Override
    public void update(Map<String, Object> map, Condition conditions) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * ない
     * @param conditions
     * @throws SQLException 
     */
    @Override
    public PreUpdate update(Condition conditions) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * ない
     * @param list
     * @throws SQLException 
     */
    @Override
    public void insert(List<Map<String, Object>> list) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> map(ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
