package net.siisise.d3bif.base;

import net.siisise.d3bif.Column;
import net.siisise.d3bif.Index;
import net.siisise.d3bif.Schema;

/**
 *
 */
public abstract class AbstractIndex implements Index {
    String name;
    Schema schema;
    Column column;
    Boolean unique;
    
    protected AbstractIndex(Schema schema, String name) {
        this.name = name;
        this.schema = schema;
    }

    protected AbstractIndex(Column column, String name) {
        this.name = name;
        this.column = column;
        // 要null対応
        this.schema = column.getTable().getSchema();
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
     * 未定
     * @return 
     */
    @Override
    public String escFullName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
