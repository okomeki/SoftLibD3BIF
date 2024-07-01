package net.siisise.d3bif.csv;

import java.sql.SQLException;
import java.util.Collection;
import net.siisise.d3bif.Catalog;
import net.siisise.d3bif.Index;
import net.siisise.d3bif.Sequence;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.base.CacheSchema;

/**
 *
 */
public class CsvSchema extends CacheSchema {


    public CsvSchema(Catalog db, String name) {
        super(db, name);
    }

    @Override
    public Collection<? extends Table> dbTables() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<? extends Index> dbIndexes() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<? extends Sequence> dbSequences() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * table 生成のみ. 管理しない.
     * @param name table名
     * @return CsvTable
     */
    @Override
    public CsvTable newTable(String name) {
        return new CsvTable(this, name);
    }

    @Override
    public Index newIndex(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Table newTable(Class cls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CsvTable dbTable(String name) throws SQLException {
        return newTable(name);
    }

    @Override
    public Index dbIndex(String name) throws SQLException {
        return newIndex(name);
    }

    @Override
    public Sequence dbSequence(String name) throws SQLException {
        return newSequence(name);
    }
    
}
