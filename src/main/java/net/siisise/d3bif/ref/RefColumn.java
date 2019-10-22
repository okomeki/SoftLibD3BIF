package net.siisise.d3bif.ref;

import net.siisise.d3bif.base.AbstractColumn;

/**
 * 定義用
 * @author okome
 */
public class RefColumn extends AbstractColumn {

    RefColumn(RefBaseTable table, String name) {
        super(table,name);
    }

    RefColumn(RefTable table, String name) {
        super(table,name);
    }

    public RefColumn(String name) {
        super(null,name);
    }
}
