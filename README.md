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
挑选 2-3 个最值得说的技术难点：
1.commit 功能的实现
commit在实现的时候需要清楚了解
2.checkout 功能的实现
chenout包含三种功能
3.merge的实现
