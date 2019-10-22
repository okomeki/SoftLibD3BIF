package net.siisise.d3bif.ref;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.siisise.d3bif.Column;
import net.siisise.d3bif.base.AbstractSchema;
import net.siisise.d3bif.Table;
import net.siisise.d3bif.annotation.ForignKey;
import net.siisise.d3bif.annotation.NotNull;
import net.siisise.d3bif.annotation.PrimaryKey;
import net.siisise.d3bif.annotation.Unique;

/**
 * 定義用
 * PostgreSQLのtemplate0の様な扱い
 * @author okome
 */
public class RefSchema extends AbstractSchema {
    
    RefSchema(RefDatabase db, String name) {
        super(db,name);
    }
    
    public RefSchema(String name) {
        super(null,name);
    }

    @Override
    public RefTable newTable(String name) {
        return new RefTable(this,name);
    }
    
    @Override
    public Table dbTable(String name) throws SQLException {
        return cacheTable(name);
    }
    
    /**
     * 定義用
     * @param name
     * @param columns
     * @return 
     */
    public RefTable table(String name, Column... columns) {
        RefTable table = new RefTable(this,name,columns);
        return table;
    }

    @Override
    public List<Table> tables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * オブジェクトを軽くテーブル定義に変換する
     * @param obj
     * @return 
     */
    public static RefBaseTable defineOf(Object obj) {
        return defineOf(obj.getClass());
    }
    
    public static RefBaseTable defineOf(String name, Map<String,Object> map) {
        RefBaseTable table = new RefBaseTable(null,name);
        for ( String key : map.keySet()) {
            Column col = table.col(key);
            Object val = map.get(key);
            col.type(val.getClass());
        }
        return table;
    }
    
    /**
     * @param cls
     * @return 
     */
    public static RefBaseTable defineOf(Class cls) {
        
        RefBaseTable table = new RefBaseTable(cls);
        Field[] fields = cls.getFields();
        for ( Field field : fields ) {
            Column col = table.col(field.getName());
            Class<?> t = field.getType();
            col.type(t);
            NotNull nkey = field.getAnnotation(NotNull.class);
            if ( nkey != null ) {
                col.NOTNULL();
            }
            Unique ukey = field.getAnnotation(Unique.class);
            if ( ukey != null ) {
                col.UNIQUE();
            }
            PrimaryKey pkey = field.getAnnotation(PrimaryKey.class);
            if ( pkey != null ) {
                col.PRIMARYKEY();
            }
            ForignKey fkey = field.getAnnotation(ForignKey.class);
            if (fkey != null ) {
                String fn = fkey.value();
                String[] fnn = fn.split("\\(|\\)");
                String tblName;
                String colName;
                if ( fnn.length == 2 ) {
                    tblName = fnn[0];
                    colName = fnn[1];
                    col.REFERENCES(new RefTable(tblName).col(colName));
                } else {
                    tblName = fnn[0];
                    col.REFERENCES(new RefTable(tblName));
                }
            }
        }
        return table;
    }

}
