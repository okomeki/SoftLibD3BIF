package net.siisise.d3bif;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * @author okome
 * @param <K> 主キーまたはUniqueなもの 1つのみ
 * @param <V> なにか
 */
public class TableMap<K,V> implements Map<K,V> {
    Table<V> tbl;
    Column keyCol;
    
    public TableMap(Table table) throws SQLException {
        this.tbl = table;
        keyCol = table.primaryKeys().get(0);
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
        return Condition.EQ(keyCol, (String)key);
    }

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
            ResultSet rs = tbl.query(condition(key));
            while ( rs.next() ) {
                return tbl.obj(rs);
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
            List<V> vs = new ArrayList();
            ResultSet rs = tbl.query();
            while ( rs.next() ) {
                vs.add(tbl.obj(rs));
            }
            return vs;
        } catch (SQLException ex) {
            Logger.getLogger(TableMap.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
