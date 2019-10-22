package net.siisise.d3bif.remote;

import net.siisise.d3bif.base.AbstractColumn;

/**
 * JDBCなColumn
 * DatabaseMetaData#getColumn
 * @author okome
 */
public class RemoteColumn extends AbstractColumn {

    protected RemoteColumn(RemoteTable table, String name) {
        super(table, name);
    }
}
