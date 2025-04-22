# OrderSystem
プログラミング応用の課題

## 概要
このリポジトリはJava 17を用いた簡易注文管理システムです。
VSCode DevContainer を用いて、チーム全員が同一環境で開発できるように設計されています。

- 使用言語:Java 17(CSE準拠)
- データベース:SQLite
- テスト:JUnit4
- UI:Swing or JavaFX(要検討)
- ビルドツール:手動(Makefile)

---

## ディレクトリ構成(仮)
```bash
├── data/              # 画像やデータベース（.db, .sql, .png など）を格納
├── out/               # コンパイル結果（.classファイル）を出力
├── lib/               # SQLiteやJUnitなどの外部ライブラリ（.jar）
├── src/               # メインのJavaコード
│   ├── controller/    # ユーザー操作の処理（例: ボタン押下時呼び出される処理）
│   ├── model/         # データモデル（例: 商品や注文などのクラス）
│   ├── util/          # DB操作や共通処理
│   └── view/          # ユーザー画面（CUI/GUIなど）
├── test/              # テストコード（JUnit など）
└── .devcontainer/     # DevContainer用設定ファイル（VS Code共有用）
```

## 使用方法(Windows上でGitのインストールからVSCodeでリポジトリを開くまで)

### Docker / Dev Container とは？

- **Docker** は、アプリケーションを動かすための環境（OS、ライブラリ、ツールなど）をまとめた「仮想的な箱（コンテナ）」を作るためのツールです。
- **Dev Container** は、Docker上に「開発環境ごとVSCodeで開ける箱」を作れる拡張機能です。

> このプロジェクトでは、**全員が同じJavaバージョン・ライブラリ構成で開発できる**ように、Dev Containerを使って環境を統一しています。

### 起動までに必要となるもの
- Git 環境(CSE標準搭載)
- Docker が動作する環境(CSE標準搭載)
- VS Code + Dev Containers 拡張機能(CSEのVS Codeでインストール必要)

### Gitのインストール
1. 以下のサイトからGitHub Desktopをダウンロード&インストール
https://desktop.github.com/
2. インストール後、GitHub Desktopを起動し、GitHubアカウントでログイン
3. ターミナルを起動&以下を実行しGitが使えることを確認
`git --version`

### Gitの初期設定
以下のコマンドを実行
```bash
git config --global user.name "GitHubアカウント名"
git config --global user.email "Githubのnoreply メールアドレス（例：12345678+username@users.noreply.github.com）"
```

### Dockerのインストール
1. 以下のサイトからDocker Desktopをダウンロード&インストール
https://www.docker.com/products/docker-desktop
2. WSL2（Windows Subsystem for Linux 2）のインストールが求められたら案内に従ってインストール
    -WSL2をインストールするとゲーム(VALORANT等)のアンチチートに引っかかるので注意
3. インストール後、Docker Desktopを起動（タスクトレイにクジラマークが出ればOK,※ログイン不要）
4. ターミナルを起動&以下を実行しDockerが使えることを確認
`docker --version`


### Dev Containersのインストール
VSCodeでDev Containersを検索してインストール

### リポジトリのダウンロード
1. リポジトリをダウンロードしたいディレクトリでターミナルを起動
2. 以下のコマンドを実行
`git clone https://github.com/haruuusame/OrderSystem.git`
3. VSCodeを`/OrderSystem/`で起動
```bash
cd ./OrderSystem/
code .
```
4. 画面右下にポップアップされる"コンテナーで再度開く"をクリック
    - 表示されなかったら画面左下の青い><をクリックし、`コンテナーで再度開く`をクリック
5. 起動が完了したらvscodeのターミナルを開き、`vscode ➜ /workspaces/OrderSystem $`と表示されていることを確認



## 開発方法(Git関連)

### GitHub上のリポジトリをローカルにクローン
- `git clone https://github.com/haruuusame/OrderSystem.git`
### 開発する機能を決める
- GitHub上でIssueから未開発の機能を見つける or 開発したい機能をIssueに登録
### 変更内容の衝突を避けるためブランチ(自分専用の作業スペース)を作成
- `git checkout -b <ブランチ名>`
    - ブランチ名の例:`feature/order-display`

#### ブランチ命名規則(例)
- `feature/<機能名>`:新機能の追加
- `fix/<修正内容>`: バグ修正
- `test/<対象>`: テストコードの追加・修正

### ブランチをリモート(GitHub上)に登録(初回push)
- `git push -u origin <ブランチ名>`
### 既存ブランチを更新
- `git pull origin <ブランチ名>`
### 変更を登録(ステージング)
```bash
git add .    #新規/変更のみ
git add -A  #削除含む全作業
```
### ファイル状態の確認
- `git status`
### ローカルにコミット(履歴に残す)
- `git commit -m "<変更内容>"`
### GitHubにコミット内容を反映(プッシュ)
- `git push origin <ブランチ名>`
### 機能が完成したらPull Request(PR)を作成
- GitHub上で「Pull request」タブを開く
- main ブランチへマージするためのPRを作成
    - PRはIssueに関係ない内容を含めない
- 他のメンバーに正しく動くかなどレビューしてもらう
- レビュー完了後、「squash and merge」でmainブランチに反映する



## Gitに含めないファイル(`.gitignore`)
- `*.class`など、コンパイルで生成されるファイル
- `.DS_Store`など、OS依存ファイル
