package net.siisise.d3bif.ref;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.base.AbstractBaseTable;

/**
 *
 * @author okome
 */
public class RefBaseTable<E> extends AbstractBaseTable {
    
    public RefBaseTable(RefSchema schema, String name, Column... columns) {
        super(schema,name);
    }

    RefBaseTable(Class<E> cls) {
        super(cls);
    }

    /**
     * 未定
     * @param column 
     */
    void add(Column column) {
        columns.put(column.getName(), column);
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
}
