package net.siisise.d3bif.ref;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.base.AbstractBaseTable;

/**
 *
 * @author okome
 */
public class RefBaseTable extends AbstractBaseTable {
    
    public RefBaseTable(RefSchema schema, String name, Column... columns) {
        super(schema,name);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
