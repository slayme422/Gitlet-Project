# Gitlet Design Document

**Name**: Kensho

## Classes and Data Structures

### Commit

#### Instance Variables

1. Message - 一次commit的后面的补充信息
2. timeStamp- 当commit被创建时候的时间
3. uid - 一个commit对象经过SHA1生成的一串编号
4. blogs- 一个Map数据结构，文件对应着uid
5. commitsDir- 专门装载commits文件
6. parents


### Repository

#### Instance Variables

1. Head - 指的是Commit的最新状态
2. master- 是一个分支名，本质上是指向某个 commit 的 指针，表示这个分支的最新 commit。
3. GITLET_DIR-GITLET的文件夹
4. COMMITS_DIR-COMMITS的文件夹
5. BLOBS_DIR-BLOBS的文件夹
6. AddStagingArea-添加暂存区，任何添加的东西都要往这里添加
7. RemoveStagingArea-删除暂存区

## Algorithms

## Persistence

