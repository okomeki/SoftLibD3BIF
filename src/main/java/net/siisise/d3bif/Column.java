package net.siisise.d3bif;

/**
 *
 */
public interface Column extends D3IfObject {
    String tableColumnName();
    
    int getType();
    String[] getTypes();

    Column type(Class<?> t);
    Column INTEGER();
    Column TEXT();
    Column VARCHAR(int size);
    // 制約
    Column NOTNULL();
    Column PRIMARYKEY();
    Column UNIQUE();
    Column REFERENCES(Table refTable);
    Column REFERENCES(Column column);

    void copy(Column srcColumn);
    int count();

    boolean isPrimaryKey();
    boolean isExportedKey();
}
