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
    
    PgTable(PgSchema schema, Class cls) {
        super(schema,cls);
    }
    
    @Override
    public PgColumn newColumn(String name) {
        return new PgColumn(this,name);
    }

}
