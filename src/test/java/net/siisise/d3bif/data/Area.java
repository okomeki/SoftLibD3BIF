package net.siisise.d3bif.data;

import net.siisise.d3bif.annotation.ForignKey;
import net.siisise.d3bif.annotation.PrimaryKey;

/**
 * テストデータの内一般的なもの
 * 位置
 * ISO 3166-1 国名コード
 * ISO 3166-2:JP 都道府県コード : JIS X 0401 都道府県コード
 * JIS X 0402 全国地方公共団体コード
 */
public class Area {
    
    @PrimaryKey
    public String code;
    @ForignKey("area(code)")
    public Area up;
    public String name;
    public String name_kana;
    
    public Area() {}
    
    public Area(String code, Area up, String name, String nameKana) {
        this.code = code;
        this.up = up;
        this.name = name;
        this.name_kana = nameKana;
    }
}
