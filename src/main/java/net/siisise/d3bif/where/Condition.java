package net.siisise.d3bif.where;

import net.siisise.d3bif.Column;

/**
 * where につけるもの
 * 戻りがbooleanっぽいものがCondition か?
 * @author okome
 */
public class Condition implements Value {
    
    static class FixValue implements Value {
        String value;

        FixValue(String val) {
            value = val;
        }
        
        @Override
        public String toString() {
            return value == null ? "null" : "'" + value + "'";
        }
        
    }
    
    static class RateValue implements Value {
        
        @Override
        public String toString() {
            return "?";
        }
    }
    
    /**
     * 直接ColumnにValueつけるかも
     */
    static class ColumnValue implements Value {
        Column col;
        
        ColumnValue(Column column) {
            col = column;
        }
        
        @Override
        public String toString() {
            return col.escFullName();
        }
    }
    
    static class IsNull extends Condition {
        Column col;
        
        IsNull(Column column) {
            col = column;
        }
        
        public String toString() {
            return col.escFullName() + " IS NULL";
        }
    }

    /**
     * and,or,代入くらいに使えるかもしれない
     */
    static class ListCondition extends Condition {
        String calc;
        Value[] values;

        private ListCondition(String calc, Value... conds) {
            this.calc = calc;
            values = conds;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            
            for ( Value val : values ) {
                if ( sb.length() != 0 ) {
                    sb.append(" ");
                    sb.append(calc);
                    sb.append(" ");
                }
                sb.append(val.toString());
            }
            sb.insert(0, "(");
            sb.append(")");
            return sb.toString();
        }
    }
    
    public static Condition EQ(Value a, Value b) {
        return new ListCondition("=",a,b);
    }
    
    public static Condition EQ(Column a, String b) {
        if ( b == null ) {
            return ISNULL(a);
        }
        return EQ(new ColumnValue(a),new FixValue(b));
    }
    
    public static Condition ISNULL(Column col) {
        return new IsNull(col);
    }
    
    public static Condition AND(Condition... conds) {
        return new ListCondition("AND",conds);
    }

    /**
     * 配列型変換のテスト
     * @param conds
     * @return 
     */
    public static Condition OR(Condition... conds) {
        return new ListCondition("OR",conds);
    }
    
    static Condition LIKE(Value a, Value b) {
        return new ListCondition("LIKE",a,b);
    }
    
}
