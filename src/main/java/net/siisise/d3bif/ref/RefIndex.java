package net.siisise.d3bif.ref;

import net.siisise.d3bif.base.AbstractIndex;

/**
 *
 */
public class RefIndex extends AbstractIndex {

    public RefIndex(RefSchema schema, String name) {
        super(schema, name);
    }

    public RefIndex(RefColumn column, String name) {
        super(column, name);
    }

}
