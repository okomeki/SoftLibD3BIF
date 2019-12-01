package net.siisise.d3bif;

/**
 * queryで使用する場合と直接値をとる場合があるかないか
 */
public interface Sequence extends D3IfObject {
    String sqlNextval();
//    long currval(); // 現在のセッションの最新の値
//    long lastval(); // 引数なし版currval
    long nextval();
//    void setval(long val);
}
