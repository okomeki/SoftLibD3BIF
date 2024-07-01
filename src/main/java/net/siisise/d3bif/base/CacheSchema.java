package net.siisise.d3bif.base;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.siisise.d3bif.Catalog;
import net.siisise.d3bif.Index;
import net.siisise.d3bif.Sequence;
import net.siisise.d3bif.Table;

/**
 *
 */
public abstract class CacheSchema extends AbstractSchema {
    protected Map<String,Table> tableMap = new HashMap<>();
    protected Map<String,Index> indexMap = new HashMap<>();
    protected Map<String,Sequence> sequenceMap = new HashMap<>();

    public CacheSchema(Catalog db, String name) {
        super(db, name);
    }

    /**
     *
     * @param name
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public Table cacheTable(String name) throws SQLException {
        Table table = tableMap.get(name);
        if (table == null) {
            table = dbTable(name);
            tableMap.put(name,table);
        }
        return table;
    }

    /**
     *
     * @param name
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public Index cacheIndex(String name) throws SQLException {
        Index index = indexMap.get(name);
        if (index == null) {
            index = dbIndex(name);
            indexMap.put(name,index);
        }
        return index;
    }
    /**
     *
     * @param name
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public Sequence cacheSequence(String name) throws SQLException {
        Sequence sequence = sequenceMap.get(name);
        if (sequence == null) {
            sequence = dbSequence(name);
            sequenceMap.put(name,sequence);
        }
        return sequence;
    }
}
