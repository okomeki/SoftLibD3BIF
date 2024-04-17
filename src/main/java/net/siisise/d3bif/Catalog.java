package net.siisise.d3bif;

import java.sql.SQLException;

/**
 * JDBCのcatalog
 * PostgreSQLっぽいなにか
 */
public interface Catalog extends D3IfObject {

    Schema schema(String name) throws SQLException;

    Schema getDefaultSchema() throws SQLException;

    Schema create(Schema src, String... options) throws SQLException;

    /**
     * 単純なコマンド類.
     * Connectionは開いて閉じる(spoolのない場合)
     *
     * @param cmd
     * @param options
     * @throws SQLException
     */
    void sql(String cmd, String... options) throws SQLException;

    String preSQL(String cmd, String... options);
}
