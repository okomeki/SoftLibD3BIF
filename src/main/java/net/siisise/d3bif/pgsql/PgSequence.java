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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String escName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String escFullName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
