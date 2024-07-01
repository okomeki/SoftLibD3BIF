package net.siisise.d3bif.pgsql;

import net.siisise.d3bif.remote.RemoteColumn;

/**
 * 何かあれば追加するかもしれない程度の実装
 */
public class PgColumn extends RemoteColumn {

    PgColumn(PgTable table, String name) {
        super(table, name);
    }

}
