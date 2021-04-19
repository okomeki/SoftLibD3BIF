package net.siisise.d3bif;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.siisise.d3bif.where.Condition;

/**
 * 未定
 * primary keyが1つのときだけ
 * 直接つかいたい
 * @param <K> 主キーまたはUniqueなもの 1つのみ, 複数の場合はObject[]配列.
 * @param <V> なにか
 */
public class TableMap<K,V> implements Map<K,V> {
    Table<V> tbl;
    Column keyCol;
    List<Column> keyCols;
    
    public TableMap(Table table) throws SQLException {
        this.tbl = table;
        keyCols = table.primaryKeys();
        keyCol = keyCols.get(0);
    }

    /**
     * なし
     * @return 
     */
    @Override
    public int size() {
        try {
            return tbl.size();
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * なし
     * @return 
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    Condition condition(Object key) {
        int i = 0;
        Object[] keys;
        if (key.getClass().isArray()) {
            keys = new Object[Array.getLength(key)];
            for ( int j = 0; j < keys.length; j++ ) {
                keys[j] = Array.get(key, j);
            }
        } else {
            keys = new Object[] {key};
        }

        Condition[] conds = new Condition[keys.length];
        for (Object k : keys ) {
            conds[i] = Condition.EQ(keyCols.get(i), (String)k);
            i++;
        }
        if ( keys.length > 1) {
            return Condition.AND(conds);
        }
        return conds[0];
    }

    /**
     * 配列の変換が起こらない気がする
     * @param key
     * @return 
     */
    @Override
    public boolean containsKey(Object key) {
        try {
            return tbl.count(condition(key)) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            return tbl.count(condition(tbl.key((V)value))) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public V get(Object key) {
        try {
            MapResultSet<V> rs = tbl.queryMap(condition(key));
            while ( rs.next() ) {
                return rs.obj();
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 
     * @param key 更新もとのキー
     * @param value 上書きするキーも含む構造
     * @return 
     */
    @Override
    public V put(K key, V value) {
        try {
            if ( containsKey(key) ) {
                tbl.update(value,condition(key));
            } else {
                tbl.insert(value);
            }
            return value;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * なし
     * @param key
     * @return 
     */
    @Override
    public V remove(Object key) {
        try {
            V v = get(key);
            tbl.remove(condition(key));
            return v;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for ( K key : m.keySet()) {
            put(key,m.get(key));
        }
    }

    @Override
    public void clear() {
        try {
            tbl.remove(null);
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Set<K> keySet() {
        try {
            Set<K> k = new HashSet<>();
            ResultSet rs = tbl.query();
            while ( rs.next() ) {
                k.add((K) rs.getString(keyCol.getName()));
            }
            return k;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Collection<V> values() {
        try {
            return tbl.obj((Condition)null);
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        try {
            Set<Entry<K, V>> vs = new HashSet<>();
            MapResultSet<V> rs = tbl.queryMap();
            while ( rs.next() ) {
                Map<String, Object> vm = rs.map();
                K k = (K)vm.get(keyCol.getName()).toString();
                vs.add(new TableEntry(k, rs.obj()));
            }
            return vs;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }
    
    class TableEntry implements Entry<K,V> {
        private K k;
        private V v;
        
        TableEntry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            v = value;
            TableMap.this.put(k, value);
            return v;
        }
    }
    
}
