package net.siisise.d3bif.data;

import net.siisise.d3bif.annotation.ForignKey;
import net.siisise.d3bif.annotation.PrimaryKey;

/**
 * テストデータの内一般的なもの
 * 位置
 * ISO 3166-1 国名コード
 * ISO 3166-2:JP 都道府県コード : JIS X 0401 都道府県コード
 * JIS X 0402 全国地方公共団体コード

 * JIS どれか 都道府県、市区町村(地方自治体)コード
 */
public class Area {
    
    @PrimaryKey
    public String code;
    @ForignKey("area(code)")
    public String up;
    public String name;
    
    public Area() {}
    
    public Area(String code, String up, String name) {
        this.code = code;
        this.up = up;
        this.name = name;
    }
}
