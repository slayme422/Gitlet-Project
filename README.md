# Gitlet
### Gitletとは？
Gitletは CS61B（UC Berkeleyのデータ構造とアルゴリズム）のGitletプロジェクト に触発されたものです。
これは授業用のプロジェクトで、Gitの基本的な機能の一部を模倣した簡易的なバージョン管理システムを実装しています。

### プロジェクト概要 / 私の貢献
このプロジェクトの完成には 約1.5か月 かかりました。
Commit、Blob、Repository クラスなどのコアコンポーネントはすべて私自身が実装しました。
このプロジェクトを通じて次のことを学びました：
1.高レベル抽象化：複数の相互作用するコンポーネントを設計。
2.データ永続化：コミットやブロブを効率的にディスク上に保存。
3.正確なGit風の動作：add、commit、checkout、branch、merge、reset などの主要なバージョン管理操作を実装。

### Gitletの使い方
Gitlet の親ディレクトリでターミナルを開き、すべてのファイルをコンパイルします: javac gitlet/*.java
Gitlet を起動するには: java gitlet.Main [init]
このプロジェクトを完成させることで、ソフトウェア設計、オブジェクト指向プログラミング、およびバージョン管理ワークフローにおける複雑なエッジケースの処理について実践的な経験を得ました。

### アルゴリズムと実装上の課題
#### コミットグラフの探索
BFS を使用してマージベースを検索。
深さ優先探索 でコミット履歴を表示。
再帰的バックトラッキング でブランチマージの競合を解決。

#### データストレージ
SHA-1 ハッシュ によるコンテンツアドレス方式。
オブジェクトのシリアライズ / デシリアライズ による永続化。
スナップショット方式の保存（差分保存ではない）。

#### マージ戦略
三者マージ（Three-way merge） アルゴリズム。
最近共通祖先 に基づく競合検出。

### More message
[詳細設計ドキュメントを見る](https://github.com/slayme422/Gitlet-Project/blob/main/gitlet-design.md)
[プロジェクト出典](https://sp21.datastructur.es/materials/proj/proj2/proj2)

# Gitlet
### what is gitlet?
Originally inspired by CS61B <Data Structures and Algorithms of the UC Berkerly> Gitlet project.Gitlet is a lesson project from cs61B. Gitlet is a version-control system that mimics some of the basic features of the popular system Git. 

### workthrough
I spent about 1.5 months completing this project.
All core components—including the Commit, Blob, Repository classes were entirely implemented by me.

### This project demonstrates:

High-level abstraction: designing a system with multiple interacting components.
Data persistence: storing commits and blobs efficiently on disk.
Accurate Git-like behavior: implementing key version-control operations such as add, commit, checkout, branch, merge, and reset.
By completing this project, I gained hands-on experience in software design, object-oriented programming, and handling complex edge cases in version-control workflows.

### How to Use Gitlet
Open a terminal in the parent directory of Gitlet, and compile all files:
javac gitlet/*.java
Run Gitlet using:
java gitlet.Main [init]

### Algorithms and Implementation Challenges
#### Commit Graph Traversal
Use BFS to find the merge base.
Display commit history using depth-first traversal.
Resolve branch merge conflicts with recursive backtracking.

#### Data Storage
SHA-1 hashing for content-addressable storage.
Serialization / deserialization for object persistence.
Snapshot-based storage instead of delta storage.

#### Merge Strategy
Three-way merge algorithm.
Conflict detection based on nearest common ancestor.
See detailed design document
Project Origin

### More message
- [design.md](https://github.com/slayme422/Gitlet-Project/blob/main/gitlet-design.md)
-[gitlet from CS61B](https://github.com/slayme422/Gitlet-Project/blob/main/gitlet-design.md)
