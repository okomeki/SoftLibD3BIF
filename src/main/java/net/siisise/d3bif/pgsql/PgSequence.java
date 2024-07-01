package net.siisise.d3bif.pgsql;

import net.siisise.d3bif.Sequence;

/**
 *
 */
public class PgSequence implements Sequence {
    String name;
    

    PgSequence(PgSchema aThis, String name) {
        this.name = name;
    }

    @Override
    public String sqlNextval() {
        return "nextval('"+name+"')";
    }

    @Override
    public long nextval() {
        //sql();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String escName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String escFullName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
