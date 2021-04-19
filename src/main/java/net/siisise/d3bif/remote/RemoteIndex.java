package net.siisise.d3bif.remote;

import net.siisise.d3bif.base.AbstractIndex;

/**
 *
 */
public class RemoteIndex extends AbstractIndex {

    /**
     * 一覧取得はできるので所属Column不明のまま作る
     * @param schema
     * @param name 
     */
    protected RemoteIndex(RemoteSchema schema, String name) {
        super(schema,name);
    }

    protected RemoteIndex(RemoteColumn column, String name) {
        super(column,name);
    }
}
