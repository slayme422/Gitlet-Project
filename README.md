# Gitlet
### what is gitlet?
Gitlet is a lesson project from cs61B Data Structures and Algorithms of the UC Berkerly. Gitlet is a version-control system that mimics some of the basic features of the popular system Git. 

### workthrough
I nearly spent 1.5 month completing this project, 这个项目考察了高度抽象化，数据持久化，精确的Git的业务逻辑，

## Classes and Data Structures

### Commit

#### Instance Variables

### Repository

#### Instance Variables

1. Head - 指的是Commit的最新状态
2. master- 是一个分支名，本质上是指向某个 commit 的 指针，表示这个分支的最新 commit。
3. GITLET_DIR-GITLET的文件夹
4. COMMITS_DIR-COMMITS的文件夹
5. BLOBS_DIR-BLOBS的文件夹
6. AddStagingArea-添加暂存区，任何添加的东西都要往这里添加
7. RemoveStagingArea-删除暂存区

包含以下功能
init-会在本地创建一个.gitlet的隐藏文件夹，就如同git的逻辑一样
add
commit
log
rm
global-log
find
status
checkout
branch
rm-branch
reset 
merge
以上所有功能的实现

# 如何使用Gitlet?
1.你需要先在父目录(gitlet的父目录)打开终端，输入javac gitlet/*.java(将所有的文件编译一边)
2.之后通过入java gitlet.Main [init]就可以开始使用

### 4.算法与难点实现
## 核心算法

### 提交图遍历
- 使用 **BFS** 查找合并基准(merge base)
- 基于 **深度优先** 的提交历史显示
- **递归回溯** 解决分支合并冲突

### 数据存储
- **SHA-1 哈希** 内容寻址
- **序列化/反序列化** 对象持久化
- **快照式存储** 而非差异存储

### 合并策略
- **三方合并** 算法
- 基于 **最近公共祖先** 的冲突检测

[查看详细设计文档](https://github.com/slayme422/Gitlet-Project/blob/main/gitlet-design.md)

### 项目出自
-[project 2 from cs61B](https://sp21.datastructur.es/materials/proj/proj2/proj2)
