package net.siisise.d3bif.base;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.siisise.d3bif.BaseTable;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.Table;
import net.siisise.json.JSONObject;

/**
 * 仮
 * PostgreSQLっぽくしておく
 */
public abstract class AbstractColumn implements Column {
    
    static class ColumnType {
        String type;
        Table table;
        Column column;
        
        ColumnType(String name) {
            type = name;
        }

        private ColumnType(String references, Table refTable) {
            type = references;
            table = refTable;
        }

        private ColumnType(String references, Column refColumn) {
            type = references;
            column = refColumn;
        }
        
        @Override
        public boolean equals(Object o) {
            if ( o instanceof ColumnType) {
                ColumnType co = (ColumnType)o;
                boolean btbl = table == null ? co.table == null : (co.table != null && table.getName().equals(co.table.getName()));
                boolean bcol = column == null ? co.column == null : (co.column != null && column.getName().equals(co.column.getName()));
                return type.equals(co.type) && btbl && bcol;
            }
            return false;
        }
        
        @Override
        public String toString() {
            if ( table != null ) {
                return type + " " + table.escName();
            } else if ( column != null ) {
                return type + " " + column.tableColumnName();
            }
            return type;
        }
    }

    // catalog tableで持つ
    
    
    /**
     * schema tableで持つ
     * nullもある
     */
    protected BaseTable table;
    // column_name
    protected String name;
    // type_name
    protected List<ColumnType> types = new ArrayList<>();
    // data_type 参考程度に
    protected int dataType;
    public boolean primaryKey;
    
    protected AbstractColumn(BaseTable table, String name) {
        this.table = table;
        this.name = name;
    }
    
    public void setTable(BaseTable table) {
        this.table = table;
    }
    
    public BaseTable getTable() {
        return table;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String escFullName() {
        if ( table == null ) {
            return escName();
        }
        return table.escFullName() + "." + escName();
    }

    @Override
    public String escName() {
        return "\"" + name + "\"";
    }
    
    @Override
    public String tableColumnName() {
        return table.escFullName() + "(" + escName() + ")";
    }
    
    @Override
    public int getType() {
        return dataType;
    }
    
    @Override
    public String[] getTypes() {
        String[] bb = new String[types.size()];
        int i = 0;
        for ( ColumnType ct : types ) {
            bb[i++] = ct.toString();
        }
        return bb;
    }

    public void setType(int type) {
        dataType = type;
    }

    public void setType(int type, String typeName) {
        dataType = type;
        types.add(new ColumnType(typeName));
    }
    
    protected void addType(String val) {
        ColumnType ct = new ColumnType(val);
        addType(ct);
    }
    
    void addType(ColumnType ct) {
        if (!types.contains(ct)) {
            types.add(ct);
        }
    }
    
    @Override
    public void copy(Column src) {
        setType(src.getType());
        if ( src instanceof AbstractColumn ) {
            AbstractColumn asrc = (AbstractColumn) src;
            primaryKey = asrc.primaryKey;
            for ( ColumnType ct : asrc.types ) {
                addType(ct);
            }
        }
    }
    
    @Override
    public int count() {
        return 0;
    }
    
    /**
     * 
     * @param t
     * @see net.siisise.d3bif.remote.RemoteTable#setList(java.sql.PreparedStatement, java.util.List, java.util.Map)
     * @return 
     */
    @Override
    public Column type(Class<?> t) {
        if ( t == String.class) {
            TEXT();
        } else if ( t == Integer.class || t == Integer.TYPE) {
            INTEGER();
        } else if ( t == java.util.Date.class) {
            DATE();
        } else {
            // 外部テーブル参照にするかもしれない
            throw new UnsupportedOperationException("まだのクラス:" +t.getName());
        }
        return this;
    }
    
    public Column type(JSONObject jo) {
        throw new UnsupportedOperationException("まだない");
    }
    
    @Override
    public Column PRIMARYKEY() {
        primaryKey = true;
        addType("PRIMARY KEY");
        if ( table != null ) {
            ((AbstractBaseTable)table).primaryKeys.add(this);
        }
        return this;
    }
    
    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public Column REFERENCES(Table refTable) {
        ColumnType ct = new ColumnType("REFERENCES",refTable);
        addType(ct);
        if ( table != null ) {
            ((AbstractBaseTable)table).importedKeys.add(this);
        }
        return this;
    }

    @Override
    public Column REFERENCES(Column refColumn) {
        ColumnType ct = new ColumnType("REFERENCES",refColumn);
        addType(ct);
        if ( table != null ) {
            ((AbstractBaseTable)table).importedKeys.add(this);
        }
        return this;
    }
    
    @Override
    public boolean isImportedKey() {
        try {
            for ( Column exKey : table.importedKeys() ) {
                if ( exKey.getName().equals(getName())) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(AbstractColumn.class.getName()).log(Level.SEVERE, null, ex);
            throw new java.lang.IllegalStateException(ex);
        }
    }
    
    @Override
    public Column importedColumn() {
        for ( ColumnType type : types ) {
            if ( type.type.equals("REFERENCES")) {
                return type.column;
            }
        }
        return null;
    }

    @Override
    public Column NOTNULL() {
        addType("NOT NULL");
        return this;
    }

    @Override
    public Column UNIQUE() {
        addType("UNIQUE");
        return this;
    }

    public Column DATE() {
        dataType = Types.DATE;
        addType("DATE");
        return this;
    }

    @Override
    public Column INTEGER() {
        dataType = Types.INTEGER;
        addType("INTEGER");
        return this;
    }

    @Override
    public Column TEXT() {
        dataType = Types.VARCHAR;
        addType("TEXT"); // PostgreSQL
        return this;
    }

    @Override
    public Column VARCHAR(int len) {
        dataType = Types.VARCHAR;
        addType("VARCHAR(" + len +")");
        return this;
    }
}
