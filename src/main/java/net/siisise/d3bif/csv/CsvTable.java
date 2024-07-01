package net.siisise.d3bif.csv;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.PreUpdate;
import net.siisise.d3bif.Schema;
import net.siisise.d3bif.base.AbstractTable;
import net.siisise.d3bif.where.Condition;

/**
 *
 */
public class CsvTable extends AbstractTable {
    
    CsvTable(Schema schema, String name) {
        super(schema, name);
    }

    @Override
    public void insert(List list) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Column> columns() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Column newColumn(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Map<String, Object> map, Condition conditions) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreUpdate update(Condition conditions) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResultSet query(Condition conditions) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int size() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int count(Condition condition) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
