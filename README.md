# MailInterfaceLibrary #

1. mvn compile → mvn install
2. 別プロジェクトで依存関係として呼び出す
3. mil.application.Applicationをimplement
4. mil.propertiesに3のパッケージ名+クラス名を記載（milsample.propertiesをコピーして作成）
5. mil.propertiesに送信、受信のメール設定を記載
6. mil.MailInterfaceStartを実行

…送信元メールアドレスからpropertiesで設定した件名のメールが送られてきた時に、その本文が3のクラスで取得できます。



後でメンテナンス