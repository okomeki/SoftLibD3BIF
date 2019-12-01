package net.siisise.d3bif.base;

import net.siisise.d3bif.Schema;
import net.siisise.d3bif.Sequence;

/**
 *
 */
public class AbstractSequence implements Sequence {
    Schema schema;
    String name;
    
    protected AbstractSequence(Schema schema, String name) {
        this.schema = schema;
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String escName() {
        return "\"" + name + "\"";
    }

    @Override
    public String escFullName() {
        return schema.escFullName() + "." + escName();
    }

    @Override
    public String sqlNextval() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long nextval() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
