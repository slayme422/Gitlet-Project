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
1.merge的实现
merge的逻辑是，把给定分支合并到当前分支，实现时候需要先准确掌握住三个地点的逻辑，分割点，给定分支，和当前分支的逻辑
#### 分割点
分割点（合并基准）是 Git 根据提交历史图找到的、两个分支的 最近公共祖先提交。它是两个分支在某个时间点最后一次相同的状态，之后它们各自有了新的提交。
#### 给定分支
是我们想要合并的其他分支，一个branch
#### 当前分支
目前我们处在的分支，即HEAD指向的分支.

在合并的时候，我们需要对每一个文件，需要先判断，他是否在分割点存在，是否在给定分支中被修改，是否在当前分支修改？
难点在，merge的逻辑有8条状态
分别是
1. 任何自分割点以来在**给定分支**中被修改，但在**当前分支**中自分割点以来**未被修改**的文件，应**更改为它们在给定分支中的版本**（从给定分支前端的提交中签出）。这些文件随后应**全部自动暂存**。
    - 澄清一下："自分割点以来在给定分支中被修改"意味着在给定分支前端的提交中存在的文件版本，与分割点处的文件版本相比，**内容不同**。请记住：blob 对象是内容寻址的！
2. 任何自分割点以来在**当前分支**中被修改，但在**给定分支**中自分割点以来**未被修改**的文件，应**保持原状**。
3. 任何自分割点以来在**当前分支和给定分支中以相同方式被修改**的文件（即，两个文件现在具有相同的内容，或者都已被删除），在合并中**保持不变**。
    - 如果一个文件在**当前分支和给定分支中都被移除**，但工作目录中存在一个同名文件，该文件将被**保留不管**，并且在合并结果中**继续保持不存在**（不被跟踪也不被暂存）。
4. 任何在**分割点不存在**，并且**仅出现在当前分支**中的文件，应**保持原状**。
5. 任何在**分割点不存在**，并且**仅出现在给定分支**中的文件，应被**签出并暂存**。
6. 任何在**分割点存在**，在**当前分支中未修改**，但在**给定分支中缺失**的文件，应被**移除**（并变为未跟踪状态）。
7. 任何在**分割点存在**，在**给定分支中未修改**，但在**当前分支中缺失**的文件，应**继续保持缺失**。
8. 任何在**当前分支和给定分支中以不同方式被修改**的文件，则处于**冲突**状态。
    - "以不同方式被修改"可以指：
        - 两个文件的内容都发生了改变，并且彼此不同；或者
        - 一个文件的内容被更改，而另一个文件被删除；或者
        - 该文件在分割点处不存在，并且在给定分支和当前分支中具有不同的内容。
    - 在这种情况下，将冲突文件的内容替换为特定的冲突标记格式（原文此处未完全列出具体格式，通常为 `<<<<<<<`, `=======`, `>>>>>>>` 等）。
如果要实现这个合并逻辑，我们需要五个boolean 变量来完成这个合并逻辑：分别是splitPointExists，modifiedInCurrent，modifiedInGiven，existsInCurrent，existsInGiven
splitPointExists:return true if the file exist in spilitPoint otherwise false
modifiedInCurrent: return true if the file in current branch is difference with the spilitPoint otherwise false;
modifiedInGiven: return true if the file in given branch is difference with the spilitPoint otherwise false;
existsInCurrent: return true if the file in currentBranch(the newest commit in the current branch)
existsInGiven: return true if the file in givenBranch;
之后通过一个boolean表格(9 x 7表格)(row,column)来表明各路逻辑。
merge功能是这些所有功能的一大难点，也是我花了很久才实现完毕的功能。
2.checkout 功能的实现
chenout包含三种功能

### 项目出自
(project 2 from cs61B) [https://sp21.datastructur.es/materials/proj/proj2/proj2]
