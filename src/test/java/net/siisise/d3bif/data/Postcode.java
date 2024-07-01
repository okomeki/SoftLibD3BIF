package net.siisise.d3bif.data;

import net.siisise.d3bif.annotation.ForignKey;

/**
 *
 */
public class Postcode {
    @ForignKey("area(code)")
    public Area area;
    
    public String code;
    public String name;
    public String name_kana;
}
