package net.siisise.d3bif.remote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import net.siisise.d3bif.PreUpdate;

public class RemotePreUpdate extends RemoteSession implements PreUpdate {

    PreparedStatement ps;

    RemotePreUpdate(RemoteCatalog db, Connection connection) {
        super(db,connection);
    }
    
}
