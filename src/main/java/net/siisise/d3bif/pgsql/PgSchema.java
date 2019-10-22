package net.siisise.d3bif.pgsql;

import net.siisise.d3bif.remote.RemoteSchema;

/**
 * することはまだない
 */
public class PgSchema extends RemoteSchema {

    PgSchema(PgDatabase db, String name) {
        super(db,name);
    }
    
    /**
     *
     * @param name
     * @return
     */
    @Override
    public PgTable newTable(String name) {
        return new PgTable(this,name);
    }

}
