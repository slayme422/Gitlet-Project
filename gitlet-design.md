# Gitlet Design Document

**Name**: Zeng XianZhao

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
4.算法与难点实现
挑选 2-3 个最值得说的技术难点：
1.寻找分割点 这个需要有一部分BFS的逻辑，因为分割点本质是两个点寻找相同的父节点,需要用到BFS算法的Marked和Queue的(poll和offer)功能

2.merge的三路合并实现 merge的逻辑是，把给定分支合并到当前分支，实现时候需要先准确掌握住三个地点的逻辑，分割点，给定分支，和当前分支的逻辑

分割点
分割点（合并基准）是 Git 根据提交历史图找到的、两个分支的 最近公共祖先提交。它是两个分支在某个时间点最后一次相同的状态，之后它们各自有了新的提交。

给定分支
是我们想要合并的其他分支，一个branch

当前分支
目前我们处在的分支，即HEAD指向的分支.

在合并的时候，我们需要对每一个文件，需要先判断，他是否在分割点存在，是否在给定分支中被修改，是否在当前分支修改？ 难点在，merge的逻辑有8条状态 分别是

任何自分割点以来在给定分支中被修改，但在当前分支中自分割点以来未被修改的文件，应更改为它们在给定分支中的版本（从给定分支前端的提交中签出）。这些文件随后应全部自动暂存。
澄清一下："自分割点以来在给定分支中被修改"意味着在给定分支前端的提交中存在的文件版本，与分割点处的文件版本相比，内容不同。请记住：blob 对象是内容寻址的！
任何自分割点以来在当前分支中被修改，但在给定分支中自分割点以来未被修改的文件，应保持原状。
任何自分割点以来在当前分支和给定分支中以相同方式被修改的文件（即，两个文件现在具有相同的内容，或者都已被删除），在合并中保持不变。
如果一个文件在当前分支和给定分支中都被移除，但工作目录中存在一个同名文件，该文件将被保留不管，并且在合并结果中继续保持不存在（不被跟踪也不被暂存）。
任何在分割点不存在，并且仅出现在当前分支中的文件，应保持原状。
任何在分割点不存在，并且仅出现在给定分支中的文件，应被签出并暂存。
任何在分割点存在，在当前分支中未修改，但在给定分支中缺失的文件，应被移除（并变为未跟踪状态）。
任何在分割点存在，在给定分支中未修改，但在当前分支中缺失的文件，应继续保持缺失。
任何在当前分支和给定分支中以不同方式被修改的文件，则处于冲突状态。
"以不同方式被修改"可以指：
两个文件的内容都发生了改变，并且彼此不同；或者
一个文件的内容被更改，而另一个文件被删除；或者
该文件在分割点处不存在，并且在给定分支和当前分支中具有不同的内容。
在这种情况下，将冲突文件的内容替换为特定的冲突标记格式（原文此处未完全列出具体格式，通常为 <<<<<<<, =======, >>>>>>> 等）。 如果要实现这个合并逻辑，我们需要五个boolean 变量来完成这个合并逻辑：分别是splitPointExists，modifiedInCurrent，modifiedInGiven，existsInCurrent，existsInGiven splitPointExists:return true if the file exist in spilitPoint otherwise false modifiedInCurrent: return true if the file in current branch is difference with the spilitPoint otherwise false; modifiedInGiven: return true if the file in given branch is difference with the spilitPoint otherwise false; existsInCurrent: return true if the file in currentBranch(the newest commit in the current branch) existsInGiven: return true if the file in givenBranch; 之后通过一个boolean表格(9 x 7表格)(row,column)来表明各路逻辑。 merge功能是这些所有功能的一大难点，也是我花了很久才实现完毕的功能。 
## Persistence

