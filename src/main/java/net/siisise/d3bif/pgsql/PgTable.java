package net.siisise.d3bif.pgsql;

import net.siisise.d3bif.remote.RemoteTable;

/**
 * 何ができるか謎のテーブル
 * 構造情報はローカルでも持つ
 * データはRemote参照
 * @author okome
 */
public class PgTable extends RemoteTable {

    PgTable(PgSchema schema, String name) {
        super(schema,name);
    }

    @Override
    public PgColumn col(String name) {
        PgColumn col = (PgColumn)columns.get(name);
        if (col == null ) {
            col = new PgColumn(this,name);
            columns.put(name, col);
        }
        return col;
    }
    
}
