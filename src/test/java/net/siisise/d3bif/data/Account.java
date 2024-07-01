package net.siisise.d3bif.data;

import net.siisise.d3bif.annotation.PrimaryKey;

/**
 *
 */
public class Account {
    @PrimaryKey
    public int id;
    public String name;
    public String hashcode;
}
