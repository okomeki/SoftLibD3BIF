package net.siisise.d3bif.base;

import java.sql.SQLException;
import net.siisise.d3bif.Catalog;
import net.siisise.d3bif.Schema;

/**
 * 抽象的な実装にするだけ
 * @author okome
 */
public abstract class AbstractCatalog implements Catalog {
    /**
     * null可かもしれない方向にする(予定)
     */
    protected String name;

    protected AbstractCatalog(String name) {
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
        return escName();
    }
    
    public String escValue(String val ) {
        return "'" + val + "'";
    }
    
    @Override
    public String preSQL(String cmd, String... options) {
        StringBuilder sb = new StringBuilder(cmd);
        for ( String option : options ) {
            sb.append(" ");
            sb.append(option);
        }
        System.out.println("preSQL: " + sb.toString());
        return sb.toString();
    }

    @Override
    public Schema create(Schema schema, String... options) throws SQLException {
        Schema xSchema = schema(schema.getName());
        sql("CREATE SCHEMA " + xSchema.escName(), options);
        return xSchema;
    }
    
}
