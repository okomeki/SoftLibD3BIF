package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.siisise.d3bif.pgsql.PgDatabase;
import net.siisise.d3bif.ref.RefColumn;
import net.siisise.d3bif.ref.RefDatabase;
import net.siisise.d3bif.ref.RefSchema;
import net.siisise.d3bif.remote.RemoteCatalog;
import net.siisise.d3bif.remote.RemoteSchema;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class TableTest {

    public TableTest() {
    }

    /**
     * ToDo: どうにかする
     */
    static RefDatabase db = new RefDatabase("さんぷる");
    static RefSchema schema = db.getDefaultSchema();
    static Column id = new RefColumn("id").INTEGER();
    static Table rfc = schema.table("rfc", new RefColumn("id").INTEGER().PRIMARYKEY(),
            new RefColumn("name").TEXT(), new RefColumn("name_ja").TEXT());

    static BaseTable area = schema.table("area", new RefColumn("code").TEXT().PRIMARYKEY(),
            new RefColumn("name").TEXT());

    static final ResourceBundle JDBCRES = ResourceBundle.getBundle("net/siisise/d3bif/jdbc");

    static String DBSERVER = JDBCRES.getString("DBSERVER");
    static String CATALOG = JDBCRES.getString("DBCATALOG");
    static String USER = JDBCRES.getString("DBUSER");
    static String PASS = JDBCRES.getString("DBPASS");

    /**
     * なんとなく
     */
    @Test
    public void testAll() {
        try {
            RemoteCatalog cat = new PgDatabase(DBSERVER, CATALOG, USER, PASS);
            RemoteSchema sc = cat.getDefaultSchema();
            //Collection<Table> dbTables = sc.dbTables();
            rfc = sc.dbTable("rfc");
            Map<String, Object> row = new HashMap<>();
            row.put("id", 1);
            row.put("name", "example");
            rfc.update(row);
            ResultSet rs = rfc.query();
            ResultSetMetaData rsmeta = rs.getMetaData();
            while (rs.next()) {
                System.out.println(rsmeta.getColumnName(1) + ":" + rs.getString(1));
                System.out.println(rsmeta.getColumnName(2) + ":" + rs.getString(2));
                System.out.println(rsmeta.getColumnName(3) + ":" + rs.getString(3));
            }
            cat.close();
        } catch (SQLException ex) {
            Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        rfc.col("no");
    }

    /**
     * Test of col method, of class BaseTable.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testCol() throws SQLException {
        System.out.println("col");
        String name = "";
        
        Schema sc = db.schema("public");
        BaseTable instance;
        instance = sc.cacheTable("rfc"); // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
        Column expResult = null;
        Column result = instance.col(name);
        //assertEquals(expResult, result);
    }

}
