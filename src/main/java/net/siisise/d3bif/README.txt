データベースの構造化

1.クラス中などに書ける構造(ref) テーブル定義 table definition
2.リモートデータベース接続用(remote)
   MetaDataから情報を構築する版
     抽象化版
     PostgreSQL実装テスト版
3.簡易でーたべーす(ndb3, あとで)

whereのところを別途?

を作ってみたい。?

DATABASE/CATALOG,SCHEMAの構造は同じにしておく?

存在しないカタログやスキーマを飛ばす方法は?

4.JSON変換
5.XML変換

接続単位
  Connection Poolを基本にする
  Connection単体でも同じIf持ちにする?

子の取得命名
Table#newColumn   Schema#newTable   新規(DBに依存しない空)
Table#dbColumn    Schema#dbTable    DB参照(毎回)
Table#cacheColumn Schema#cacheTable DB参照(キャッシュあり)
