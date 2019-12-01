package net.siisise.d3bif.ref;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.base.AbstractBaseTable;

/**
 *
 * @param <E>
 */
public class RefBaseTable<E> extends AbstractBaseTable {
    
    public RefBaseTable(RefSchema schema, String name, Column... columns) {
        super(schema,name,columns);
    }

    RefBaseTable(RefSchema schema, Class<E> cls) {
        super(schema, cls);
    }

    /**
     * 未定
     * @param column 
     */
    void add(Column column) {
        columns.put(column.getName(), column);
    }

    @Override
    public RefColumn newColumn(String name) {
        return new RefColumn(this,name);
    }

    @Override
    public RefColumn col(String name) {
        RefColumn col = (RefColumn)columns.get(name);
        if ( col == null ) {
            col = newColumn(name);
            columns.put(name, col);
        }
        return col;
    }

    @Override
    public Collection<Column> columns() throws SQLException {
        return new ArrayList<>(columns.values());
    }

    @Override
    public void drop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
