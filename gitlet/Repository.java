package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author ZengXianZhao
 */
public class Repository {
    /**
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /*HEAD always points to the newest Commit**/
    public static Commit HEAD;

    /*** master always point at newest commit*/
    public static Commit master;

    /**The current working directory.*/
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**The .gitlet directory.*/
    public static final File GITLET_DIR = join(CWD, ".gitlet");  //创建了个gitlet文件夹

    /**The directory which saves commits file*/
    public static final File COMMITS_DIR =join(GITLET_DIR,"commits");

    /**The directory which saves blobs dir*/
    public static final File BLOBS_DIR=join(GITLET_DIR,"blobs");

    /**ADD_STAGING_FILE which made AddStagingArea persistence*/
    static final File ADD_STAGING_FILE = new File(GITLET_DIR, "add_staging");

    /**REMOVE_STAGING_File which made RemoveStagingArea persistence*/
    static final File REMOVE_STAGING_FILE = new File(GITLET_DIR, "remove_staging");

    /**The dic which saves the branches*/
    static final File BRANCH_DIR=new File(GITLET_DIR,"branches");
    /** 每个b文件名和他所对应的blob的uid*/
    private HashMap<String, String> AddStagingArea = new HashMap<>();//

    //删除的文件名和他对应的blob uid
    private HashMap<String,String> RemoveStagingArea = new HashMap<>();


    /***
     * It will have a single branch: master,
     * which initially points to this initial commit, and master will be the current branch.
     */
    public void init() {
        if (GITLET_DIR.exists()) {
            throw new RuntimeException("A Gitlet version-control system already exists in the current directory.");

        }
        GITLET_DIR.mkdirs();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCH_DIR.mkdir();

        Commit initial = new Commit("initial commit", null, null, new HashMap<>());
        initial.setTimeStamp(new Date(0));

        //保存提交
        initial.saveCommit();


        File Master_FIle=join(BRANCH_DIR, "master");
        Utils.writeContents(Master_FIle, initial.getUid());

        File headFile = Utils.join(GITLET_DIR, "HEAD");
        Utils.writeContents(headFile, "master");
    }

    /**添加文件到暂存区里
     * 注意:如果文件和存储的文件相同，即blog uid相同，则不添加
     */
    public void add(String fileName) {
        //去搜索这个文件
        File file = new File(CWD, fileName);

        if (!file.exists()) {
            throw new RuntimeException("File error, file does not exist");
        }

        Blob blob = new Blob(file);//进入Blob类型的有参构造

        //假设:
        //add file.txt 与暂存区的blob文件uid相同，不添加。不相同才添加
        AddStagingArea.put(fileName, blob.uid);

        Utils.writeObject(ADD_STAGING_FILE, AddStagingArea);
        Utils.writeObject(REMOVE_STAGING_FILE, RemoveStagingArea);
    }

    /**Commit 提交
     * 把暂存区所有的文件提交，生成一个commit文件保存在电脑里
     */
    public void commit(String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (ADD_STAGING_FILE.exists()) {
            AddStagingArea = Utils.readObject(ADD_STAGING_FILE, HashMap.class);
        }
        if (REMOVE_STAGING_FILE.exists()) {
            RemoveStagingArea = Utils.readObject(REMOVE_STAGING_FILE, HashMap.class);
        }
        // 检查暂存区是否为空（应该同时检查添加和删除暂存区）
        if (AddStagingArea.isEmpty() && RemoveStagingArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        HEAD=getHeadCommit();
        // 🔥 修复：从文件读取 HEAD，而不是依赖内存字段
        String parentUid =HEAD.getUid();

        // 创建一个新的commit
        Commit newCommit = new Commit(message, parentUid, AddStagingArea, RemoveStagingArea);
        newCommit.saveCommit();

        // 清空暂存区
        AddStagingArea.clear();
        RemoveStagingArea.clear();
        Utils.writeObject(ADD_STAGING_FILE, AddStagingArea);
        Utils.writeObject(REMOVE_STAGING_FILE, RemoveStagingArea);

        // 更新HEAD
        HEAD = newCommit;
        master = HEAD;

        // 🔥 关键修复：保存整个HEAD的uid
        File HEAD_FILE = join(GITLET_DIR, "HEAD");

        //在.gitlet目录下的HEAD文件里写进HEAD的UID
        Utils.writeObject(HEAD_FILE, HEAD.getUid());

    }



    /**rm
     * 如果该文件当前被暂存用于添加（staged for addition），则取消暂存（从暂存区中移除）。
     * 如果该文件在当前 commit 中被跟踪（tracked），则将其标记为删除（stage it for removal），
     * 并从工作目录中删除该文件（前提是用户尚未手动删除它）。
     * 注意：只有当文件被当前 commit 跟踪时才会从工作目录删除
     *
     * @param fileName 指定的文件
     */
    public void rm(String fileName) {
        HEAD=getHeadCommit();

        Map<String,String> trackedFiles=HEAD.getTrackedFiles();
        if (!AddStagingArea.containsKey(fileName)&&!trackedFiles.containsKey(fileName)){
            System.out.println("No need to remove file");
            return;
        }

        //1.检查暂存区
        if (AddStagingArea.containsKey(fileName)) {
            AddStagingArea.remove(fileName);
        }
        //2.检查是否已经被跟踪(Tracked in HEAD commit)
        if(trackedFiles.containsKey(fileName)){
            File file=new File(CWD,fileName);
            if(file.exists()){
                file.delete();
            }
            RemoveStagingArea.put(fileName,null);
        }
        // 如果既不在暂存区也不被跟踪
        Utils.writeObject(ADD_STAGING_FILE, AddStagingArea);
        Utils.writeObject(REMOVE_STAGING_FILE, RemoveStagingArea);
    }

    /**Git log  1.从HEAD指针开始依次打印每条信息*/
    public void log() {
        HEAD=getHeadCommit();

        Commit current = HEAD;

        while (current != null) {
            // 在 log 方法中添加调试信息
            while (current != null) {
                printCommitInfo(current);

                if (current.parents.isEmpty()) {
                    current = null;
                } else {
                    String parentHash = current.parents.get(0);
                    current = getCommit(parentHash);
                }
            }
        }

    }


    /**globalLog把所有commits无序的打印出来*/
    public void globalLog() {
        List<String> files = Utils.plainFilenamesIn(COMMITS_DIR);
        for (String file : files) {
            //读取file的string名转换成commits文件
            Commit cur = getCommit(file);

            Date commitTime = cur.timeStamp;

            // Git 时间格式
            SimpleDateFormat gitFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
            gitFormat.setTimeZone(TimeZone.getTimeZone("PST"));

            String gitTime = gitFormat.format(commitTime);

            System.out.println("===");
            System.out.println("commit: " + cur.getUid());
            System.out.println("Date: " + gitTime);
            System.out.println(cur.message);
            System.out.println();

        }
    }

        /**find 命令就是：
         * 1.遍历所有 commit
         * 2.找 message 完全匹配的 commit
         * 3.打印它们的 id
         * 4.没找到就输出错误信息
         */
     public void find(String message){
            List<String>files=Utils.plainFilenamesIn(COMMITS_DIR);
            boolean found=false;
            for (String file : files) {
                Commit cur = getCommit(file);
                if (cur.message.equals(message)) {
                    System.out.println(cur.getUid());
                    found = true;
                }
            }
            if(!found){
                System.out.println("Found no commit with that message.");
                }
            }

     /**java gitlet.Main checkout -- [file name]
      * 1.取出 HEAD 提交中文件存在的版本，将其放入工作目录，
      * 并覆盖工作目录中已存在的该文件版本（如果有）。文件的新版本不会被添加到暂存区。*/
     public void checkout(String fileName){
         //读取Head
         HEAD=getHeadCommit();

         Map<String,String> trackedFiles= HEAD.getTrackedFiles();
         if (!trackedFiles.containsKey(fileName)) {
             System.out.println("File doesn't exist in that commit");
             return;
         }
         //获取文件Commit HEAD里指定的fileName存档
         String blobUid=trackedFiles.get(fileName);

         //获取那个文件的blob
         Blob blob =getBlob(blobUid);

         File targetFile=Utils.join(CWD,fileName);
         // 确保父目录存在
         if (targetFile.getParentFile() != null) {
             targetFile.getParentFile().mkdirs();}

         //去覆盖
         Utils.writeContents(targetFile,blob.getContent());
         }

     /**java gitlet.Main checkout -- [commitID, file name]
      * 我们想要获取这个指定commit时候的文件*/
     public void checkout(String commitID, String fileName) {
         // 1.先把这个commitID在.gitlet/commits/ 下方找到对应的文件
         File targetCommit=Utils.join(COMMITS_DIR,commitID);
         if (!targetCommit.exists()) {
             System.out.println("Commit doesn't exist ");
             return;
         }
         Commit head = getHeadCommit();
         Map<String, String> headTracked = head.getTrackedFiles();

         File target = Utils.join(CWD, fileName);
         if (target.exists() && !headTracked.containsKey(fileName)) {
             System.out.println("There is an untracked file in the way; delete it or add it first.");
             return;
         }
         Commit thatCommit= getCommit(commitID);
         Map<String,String> thatTrackedFiles =thatCommit.getTrackedFiles();
         if (!thatTrackedFiles.containsKey(fileName)) {
             System.out.println("File doesn't exist in that commit");
             return;
         }
         //说明那个commit里面文件存在
         String blobUid= thatTrackedFiles.get(fileName);

         //获取那个文件的blob
         Blob blob =getBlob(blobUid);

         File targetFile=Utils.join(CWD,fileName);
         // 确保父目录存在
         if (targetFile.getParentFile() != null) {
             targetFile.getParentFile().mkdirs();}

         //去覆盖
         Utils.writeContents(targetFile,blob.getContent());
     }

     /**java gitlet.Main checkout [branch name]*/
     public void checkoutBranch(String branchName){
        switchBranch(branchName);
     }

     /**
      * 将指定分支（branch）的 HEAD commit 中的所有文件放入工作目录（working directory）
      * 如果工作目录中已经存在同名文件，会被覆盖
      * 命令执行完毕后，指定分支将被视为当前分支（HEAD）
      * 当前分支中被跟踪（tracked）但在被检出的分支中不存在的文件会被删除
      * 暂存区（staging area）会被清空，除非被检出的分支就是当前分支（见失败情况说明*/
     public void switchBranch(String branchName) {
         if(!canSwitchBranch(branchName)){
             return;
         }

         File branchFile = Utils.join(BRANCH_DIR, branchName);
         // 1. 先保存当前状态（在切换前）
         Commit currentCommit = getHeadCommit();
         Map<String, String> currentTracked = currentCommit.getTrackedFiles();

         // 2. 读取目标commit
         String branchUid = Utils.readContentsAsString(branchFile).trim();
         Commit targetCommit = getCommit(branchUid);
         Map<String, String> targetTracked = targetCommit.getTrackedFiles();

         // 3. 恢复目标分支的文件
         for (String fileName : targetTracked.keySet()) {
             String blobUid = targetTracked.get(fileName);
             Blob blob = getBlob(blobUid);
             String blobContent = blob.getContent();

             File targetFile = Utils.join(CWD, fileName);
             Utils.writeContents(targetFile, blobContent);
         }

         // 4. 删除当前分支有但目标分支没有的文件
         for (String fileName : currentTracked.keySet()) {
             if (!targetTracked.containsKey(fileName)) {
                 File fileToDelete = Utils.join(CWD, fileName);
                 if (fileToDelete.exists()) {
                     fileToDelete.delete();
                 }
             }
         }

         // 5. 更新HEAD和清空暂存区
         File headFile = Utils.join(GITLET_DIR, "HEAD");
         Utils.writeContents(headFile, branchName);

         // 清空暂存区
         clearStagingArea();
     }

     /**同git status逻辑*/
     public void status(){
         System.out.println("=== Branches ===");

         List<String>branchesName=Utils.plainFilenamesIn(BRANCH_DIR);
         String currentBranch = Utils.readContentsAsString(Utils.join(GITLET_DIR, "HEAD")).trim();
         for (String branchName : branchesName) {
             if (currentBranch.equals(branchName)) {
                 System.out.println("*" + branchName);
             } else {
                 System.out.println(branchName);
             }
         }

         System.out.println("=== Staged Files ===");
         AddStagingArea = ADD_STAGING_FILE.exists()
                 ? Utils.readObject(ADD_STAGING_FILE, HashMap.class)
                 : new HashMap<>();
         for (String fileName : AddStagingArea.keySet()) {
             System.out.println(fileName);
         }

         System.out.println("=== Removed Files ===");
         RemoveStagingArea = REMOVE_STAGING_FILE.exists()
                 ? Utils.readObject(REMOVE_STAGING_FILE, HashMap.class)
                 : new HashMap<>();
         for (String fileName : RemoveStagingArea.keySet()) {
             System.out.println(fileName);
         }
}

     /**jave gitlet.Main branch [branch name]
      * 创建一个branch Name 应该就是Commit类型*/

     public void branch(String branch){
         //读取HEAD指针
         HEAD=getHeadCommit();

         //在.gitlet下方创建一个新的文件，名字是branch的名字，里面记载着uid
         File newBranchFile =Utils.join(BRANCH_DIR,branch);

         if (newBranchFile.exists()) {
             System.out.println("A branch with that name already exists.");
             return;
         }

         Utils.writeContents(newBranchFile,HEAD.getUid());
     }

    /**java gitlet.Main rm-branch [branch name]*/
    public void removeBranch(String branch){
        //根据输入的branch删除具体文件
        //具体文件怎么找
        File targetFile=Utils.join(BRANCH_DIR,branch);
        if (!targetFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        //如果目前的branch和输入的branch相同，则返回Cannot remove the current branch.
        String currentBranch=readCurrentBranch();
        if(currentBranch.equals(branch)){
            System.out.println("Cannot remove the current branch.");
            return;
        }

        targetFile.delete();

    }

    /**java gitlet.Main reset [commit id]*/
    public void reset(String commitID){
        //边界情况，如果commitID不存在具体的文件夹里就显示
        isCommitExists(commitID);

        checkUntrackedFile(commitID);

        //检查如果当前的文件夹
        List<String> listFiles =Utils.plainFilenamesIn(CWD);

        Map<String,String>targetTrackedFiles=getTrackedFiles(commitID);
        for (String fileName : listFiles) {
            if (!targetTrackedFiles.containsKey(fileName)) {
                File fileToDelete = Utils.join(CWD, fileName);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }

        }
        // 5. 【缺少的关键步骤】将目标提交的文件恢复到工作区
        restoreFilesFromCommit(commitID);

        updateCurrentBranch(commitID);

        clearStagingArea();

        }

    /**java gitlet.Main merge [branch name]*/
    public void merge(String branch){
        //if stagingArea is not empty, Return
        if(!isStagingAreaEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        //边界情况，如果commitID不存在具体的文件夹里就显示


        Commit currentBranch=getCurrentBranch();
        Commit givenBranch =getCommit(branch);
        Commit splitPoint=findSplitPoint(currentBranch, givenBranch);

        isCommitExists(currentBranch.getUid());

        checkUntrackedFile(currentBranch.getUid());
        //如果当前节点等于分界点，快进当前节点
        if(currentBranch.equals(splitPoint)){
            checkoutBranch(branch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if(givenBranch.equals(splitPoint)){
            System.out.println("Given branch is an ancestor of the current branch");
            return;
        }
        //将splitPoint, current,givenBranch，三个状态的文件全部
        //存放到allFiles里面
        HashSet<String> allFiles=new HashSet<>();
        //文件存在分界点vs文件不在分界点存在

        allFiles.addAll(allFilesIntoSet(currentBranch));
        allFiles.addAll(allFilesIntoSet(splitPoint));
        allFiles.addAll(allFilesIntoSet(givenBranch));


        Map<String,String> currentBranchTargetFiles=currentBranch.getTrackedFiles();
        Map<String,String > givenBranchTargetFiles = givenBranch.getTrackedFiles();

        for (String fileName : allFiles) {
            /**
             * spilitPointExists-fileName文件在分割点存在
             * modifiedInCurrent-fileName文件在当前分支被修改了
             * modifiiedInGiven-true则fileName文件在给定分支被修改了，false则未修改
             * existsInCurrent-true在当前分支中存在, false则不存在
             * existsInGiven-true 在给定分支中存在，false则不存在*/
            boolean spilitPointExists = isFileInSplitPoint(splitPoint, fileName);
            boolean modifiedInCurrent = compareWithSplitPoint(fileName, splitPoint, currentBranch);
            boolean modifiedInGiven = compareWithSplitPoint(fileName, splitPoint, givenBranch);
            boolean existsInCurrent = currentBranchTargetFiles.containsKey(fileName);
            boolean existsInGiven = givenBranchTargetFiles.containsKey(fileName);
            boolean hasConflict = false;

            //规则1: 在给定分支中被修改,但在当前分支没被修改的文件
            if (spilitPointExists  && !modifiedInCurrent  && modifiedInGiven) {
                //获取在给定分支中的版本blob-uid
                String blobUid = givenBranchTargetFiles.get(fileName);
                //在硬盘是在blob文件下的
                AddStagingArea.put(fileName, blobUid);


                //2.任何自分割点以来在当前分支中被修改，但在给定分支中自分割点以来未被修改的文件，应保持原状。

            } else if (spilitPointExists &&modifiedInCurrent  && !modifiedInGiven ) {
                continue;

                /**3. 任何自分割点以来在**当前分支和给定分支中以相同方式被修改**的文件
                 * （即，两个文件现在具有相同的内容，或者都已被删除），在合并中**保持不变**。
                 - 如果一个文件在**当前分支和给定分支中都被移除**，但工作目录中存在一个同名文件，
                 该文件将被**保留不管**，并且在合并结果中**继续保持不存在**（不被跟踪也不被暂存）。*/
            } else if (spilitPointExists  && modifiedInGiven&& modifiedInCurrent ) {
                //获得当前文件的blobUID
                String gbFileBlobID = givenBranchTargetFiles.get(fileName);
                String cbFileBlobID = currentBranchTargetFiles.get(fileName);
                if (gbFileBlobID.equals(cbFileBlobID)) {
                    continue;
                }else{//8.任何在当前分支和给定分支中以不同方式被修改的文件，则处于冲突状态。
                    System.out.println(">>>>>>>"+readCurrentBranch());
                    createConflictFile(fileName,branch);
                }
            }//4. 任何在分割点不存在，并且仅出现在当前分支中的文件，应保持原状。
            else if (!spilitPointExists  && !modifiedInGiven  && modifiedInCurrent ) {
                continue;
            }//5.任何在分割点不存在，并且仅出现在给定分支中的文件，应被签出并暂存。
            else if(!spilitPointExists  && modifiedInGiven  && !modifiedInCurrent ) {
                checkout(branch,fileName);
                //获取在给定分支中的版本blob-uid
                String blobUid = givenBranchTargetFiles.get(fileName);
                //在硬盘是在blob文件下的
                AddStagingArea.put(fileName, blobUid);
            }//6.任何在分割点存在，在当前分支中未修改，但在给定分支中缺失的文件，应被移除（并变为未跟踪状态）。
            else if (spilitPointExists  && !modifiedInCurrent && !existsInGiven) {
                File fileToDelete = Utils.join(CWD, fileName);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
                //未跟踪状态
                AddStagingArea.remove(fileName);
                // 3. 如果你的Gitlet有删除暂存区，就添加到那里
                //    这样在下次提交时，这个文件就会从跟踪文件中移除
                if (RemoveStagingArea != null) {
                    RemoveStagingArea.put(fileName,null);
                }
            }
            //7.任何在分割点存在，在给定分支中未修改，但在当前分支中缺失的文件，应继续保持缺失。
            else if(spilitPointExists && !modifiedInGiven && !existsInCurrent){
                continue;
            }
            else {
                createConflictFile(fileName, branch);
                hasConflict = true;
            }


        }
        Utils.writeObject(ADD_STAGING_FILE, AddStagingArea);
        // 提交合并
        commitMerge(currentBranch, givenBranch, "Merged " + branch);

        // 如果有冲突，打印消息
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public void commitMerge(String currentbranch, String fileName) {  }
    public void createConflictFile(String fileName, String givenBranch){
        //先获取HEAD的trackedFiles
        Map<String,String>trackedFiles=getCurrentTrackedFiles();
        String fileUid=trackedFiles.get(fileName);
        //对准具体文件
        Blob conflictBlob=getBlob(fileUid);
        //获取conflictBlob的content
        String content=conflictBlob.getContent();

        //获取givenBranch的trackedFiles
        Map<String,String>givenBranchTrackedFiles=getTrackedFiles(givenBranch);
        String gBFileUid=givenBranchTrackedFiles.get(fileName);
        //获取givenBranch中的File文件的UID
        Blob gbFile=getBlob(gBFileUid);
        String content2=gbFile.getContent();
        String conflictContent = "<<<<<<< HEAD\n" +
                content +
                "=======\n" +
                content2 +
                ">>>>>>>\n";

        File file=Utils.join(CWD, fileName);
        Utils.writeContents(file,conflictContent);
        stageFile(fileName);
    }
    //
    private void stageFile(String fileName) {
        // 计算文件的blob ID
        File file = Utils.join(CWD, fileName);
        byte[] content = Utils.readContents(file);
        String blobId = Utils.sha1(content);

        // 保存blob到 .gitlet/blobs/ 文件夹
        File blobFile = Utils.join(BLOBS_DIR, blobId);
        Utils.writeContents(blobFile, content);

        // 添加到暂存区（仅此一步！）
        AddStagingArea.put(fileName, blobId);

    }
    /**查看有哪些文件与分界点不同*/
    public boolean compareWithSplitPoint(String file,Commit splitPoint,Commit branch){

        if (splitPoint == null || branch == null) {
            return true; // 或者根据你的需求返回 false/抛出异常
        }
        //获取分离点的trackedFiles的map列表
        Map<String,String>spTrackedList=splitPoint.getTrackedFiles();
        //获取branch的trackedFiles的map'列表
        Map<String,String>branchTrackedList=branch.getTrackedFiles();

        // 获取文件的blob ID，如果文件不存在则返回null
        String splitBlob = spTrackedList.get(file);
        String branchBlob = branchTrackedList.get(file);
        //对比两个commit的sha1
        return !Objects.equals(splitBlob, branchBlob);

    }
    public Commit findSplitPoint(Commit head1, Commit head2) {
        //String branch和Currentbranch都从头往父节点遍历，并且放入到linkedList里
        Queue<Commit> queue1 = new LinkedList<>();
        Queue<Commit> queue2 = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();

        queue1.offer(head1);
        queue2.offer(head2);
        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            if (!queue1.isEmpty()) {
                Commit commit = queue1.poll();
                String commitUid = commit.getUid();
                if (visited.contains(commitUid)) {
                    return commit;
                }
                visited.add(commitUid);

                for (String parent : commit.parents) {
                    if (parent != null) {
                        Commit parentCommit = getCommit(parent);
                        queue1.add(parentCommit);
                    }
                }
            }
            if (!queue2.isEmpty()) {
                Commit commit = queue2.poll();
                String commitUid = commit.getUid();
                if (visited.contains(commitUid)) {
                    return commit;
                }
                visited.add(commitUid);
                for (String parent : commit.parents) {
                    if (parent != null) {
                        Commit parentCommit = getCommit(parent);
                        queue2.add(parentCommit);
                    }
                }
            }
        }
        return null;

    }
    /**通过curBranch的名字来返回最新Commit*/
    public Commit getCurrentBranch(){
        String curBranch=readCurrentBranch();//返回的是branch名字
        File currentBranchFile=Utils.join(BRANCH_DIR, curBranch);
        String commitUid=Utils.readContentsAsString(currentBranchFile);
        return getCommit(commitUid);
    }

    public Commit getBranchCommit(String branch){
        File currentBranchFile=Utils.join(BRANCH_DIR, branch);
        String commitUid=Utils.readContentsAsString(currentBranchFile);
        return getCommit(commitUid);

    }
     /**
      * 以下函数都是helperFunction，与主逻辑函数无关
      */

    /**
     * Returns the Blob object corresponding to the given blob UID.
     * @param blobUid the UID of the blob
     * @return the blob object
     */
     public Blob getBlob(String blobUid){
             File blobfile=Utils.join(BLOBS_DIR, blobUid);
             if (!blobfile.exists()) {
                 return null;
             }
             return Utils.readObject(blobfile, Blob.class);
     }

     /**
      * Returns the head Object
      *
      * @return the head Object or null if the head file doesn't exist
      */
    public Commit getHeadCommit() {
        File headFile = Utils.join(GITLET_DIR, "HEAD");

        // HEAD 不存在 → repo 不存在 → 返回 null，不打印
        if (!headFile.exists()) {
            return null;
        }

        // 读取分支名
        String branchName = Utils.readContentsAsString(headFile).trim();

        if (branchName.length() == 0) {
            return null;
        }

        // 通过branchName获取分支文件 - 使用 Utils.join 更安全
        File branchFile = Utils.join(BRANCH_DIR, branchName);

        // 检查分支文件是否存在
        if (!branchFile.exists()) {
            return null;
        }

        String branchUid = Utils.readContentsAsString(branchFile).trim();

        // 通过 commit SHA-1 获取 commit 对象
        Commit headCommit = getCommit(branchUid);

        return headCommit;   // 如果不存在，返回 null，但不打印
    }

    // 提取方法，提高可读性
    private void printCommitInfo(Commit commit) {
        SimpleDateFormat gitFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        gitFormat.setTimeZone(TimeZone.getTimeZone("PST"));

        String formattedTime =gitFormat.format(commit.timeStamp);

        System.out.println("===");
        System.out.println("commit " + commit.getUid());
        System.out.println("Date: " + formattedTime);
        System.out.println(commit.message);
        System.out.println();
    }

    public static boolean containsCommit(String commitUid) {
        if (commitUid == null) return false;

        // 根据提交UID构建文件路径
        File commitFile = join(COMMITS_DIR, commitUid);

        // 检查提交文件是否存在
        return commitFile.exists();
    }

    public static File getObjectFile(String hash){
        return Utils.join(COMMITS_DIR,hash);
    }

    /**为了获得 父类的commit信息的Helper Function
     * 参数commitHash ，通过字符串commitHash转换成Commit类
     * 返回一个Commit 父类*/
    public static Commit getCommit(String commitHash){
        // 1. 处理 null 在init情况下
        if (commitHash == null) {
            return null;
        }
        //获取commitHash的文件
        File commitFile=getObjectFile(commitHash);

        return Utils.readObject(commitFile,Commit.class);
    }

    private void clearStagingArea(){
        AddStagingArea=Utils.readObject(ADD_STAGING_FILE, HashMap.class);
        RemoveStagingArea=Utils.readObject(REMOVE_STAGING_FILE, HashMap.class);

        AddStagingArea.clear();
        RemoveStagingArea.clear();

        Utils.writeContents(ADD_STAGING_FILE, AddStagingArea);
        Utils.writeContents(REMOVE_STAGING_FILE, RemoveStagingArea);
    }

    private boolean canSwitchBranch(String branchName){
        String currentBranch = Utils.readContentsAsString(Utils.join(GITLET_DIR, "HEAD")).trim();
        if (currentBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return false;
        }

        File branchFile = Utils.join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return false;
        }
        return true;
    }

    //读取目前所在的branch名字(string type)()不是uid也不是commit类型
    private String readCurrentBranch(){
        return Utils.readContentsAsString(Utils.join(GITLET_DIR, "HEAD"));
    }


    private void isCommitExists(String commitId){
        File targetCommitFile = Utils.join(COMMITS_DIR, commitId);
        if (!targetCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
        }
    }


    /**
     * 在重置前，要检查当前工作区是否有未跟踪的文件，如果没被跟踪，
     * 且在要切回的commit里被追踪了的话，就要提示
     * There is an untracked file in the way; delete it, or add and commit it first.
     */
    private void checkUntrackedFile(String commitID){

        Map<String,String>targetTrackedFiles=getTrackedFiles(commitID);
        List<String> listFiles =Utils.plainFilenamesIn(CWD);

        Map<String,String>currentTrackedFiles=getCurrentTrackedFiles();

        /**
         * 2.在重置前，要检查当前工作区是否有未跟踪的文件，如果没被跟踪，
         * 且在要切回的commit里被追踪了的话，就要提示
         * There is an untracked file in the way; delete it, or add and commit it first.
         * */
        for (String fileName : listFiles) {
            //检查所有文件是否被追踪
            boolean isCurrentlyTracked = currentTrackedFiles.containsKey(fileName);
            //会被覆盖
            boolean willWriteOver = targetTrackedFiles.containsKey(fileName);
            if(!isCurrentlyTracked && willWriteOver){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    /**Return the currentTrackedFiles
     *
     * @return a Map where the keys are file names and the values are file hashes(SHA-1)
     * If there is no HEAD commit (i.e., no commits yet), returns an empty Map*/
    private Map<String, String> getCurrentTrackedFiles() {
        Commit headCommit = getHeadCommit();  // 修正：使用局部变量，不要修改HEAD
        if (headCommit == null) {
            return new HashMap<>(); // 如果没有提交，返回空Map
        }
        return headCommit.getTrackedFiles();
    }


    private Map<String, String> getTrackedFiles(String commitID) {
        Commit targetCommit=getCommit(commitID);
        return targetCommit.getTrackedFiles();
    }

    /**从那个commit里恢复文件*/
    private void restoreFilesFromCommit(String commitID) {
        // 获取目标提交
        Commit targetCommit = getCommit(commitID);
        Map<String, String> targetTrackedFiles = targetCommit.getTrackedFiles();

        for (String fileName : targetTrackedFiles.keySet()) {
            // 定位工作区文件
            File file = Utils.join(CWD, fileName);
            String blobUid = targetTrackedFiles.get(fileName);

            // 获取blob内容
            Blob targetBlob = getBlob(blobUid);
            String content = targetBlob.getContent();

            // 写入文件（确保父目录存在）
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();  // ✅ 创建必要的目录结构
            }

            Utils.writeContents(file, content);
        }
    }

    /** 往branches文件夹下的branch的文件放进UID*/
    private void updateCurrentBranch(String commitID){
        String currentBranch=readCurrentBranch();
        File branchFile = Utils.join(BRANCH_DIR, currentBranch);
        Utils.writeContents(branchFile, commitID);
    }

    /**检查暂存区有东西(添加区和删除区)
     * @return return false If there are staged additions or removals present
     *               return true if there are nothing in the staged Area*/
    private boolean isStagingAreaEmpty() {
        AddStagingArea = Utils.readObject(ADD_STAGING_FILE, HashMap.class);
        RemoveStagingArea = Utils.readObject(REMOVE_STAGING_FILE, HashMap.class);

        // 防止 null 导致 NPE
        if (AddStagingArea == null) AddStagingArea = new HashMap<>();
        if (RemoveStagingArea == null) RemoveStagingArea = new HashMap<>();

        return AddStagingArea.isEmpty() && RemoveStagingArea.isEmpty();
        }

    /**返回特定Commit下的包含着所有文件名的列表集合*/
    private Set<String> allFilesIntoSet(Commit commit){
        Set<String> commitFilesSet=commit.getTrackedFiles().keySet();
        return commitFilesSet;
    }
    /**检查spCommit(SplitPoint)是否包含File文件
     * @return true 如果 SplitPoint包含, false 代表不包含*/
    public boolean isFileInSplitPoint(Commit spCommit, String fileName){
        Set<String>commitTrackedFiles= spCommit.getTrackedFiles().keySet();
        if(commitTrackedFiles.contains(fileName)){
            return true;
        }
        return false;
    }

}