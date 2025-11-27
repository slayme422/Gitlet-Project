    package gitlet;

    // TODO: any imports you need here

    import java.io.File;
    import java.io.Serializable;
    import java.util.ArrayList;
    import java.util.Date; // TODO: You'll likely use this in this class
    import java.util.HashMap;
    import java.util.Map;

    import static gitlet.Utils.join;

    /** Represents a gitlet commit object.
     *  commit是一个类对象把文件提交到
     *
     *  @author TODO
     */
    public class Commit implements Serializable {

        public static final File CWD = new File(System.getProperty("user.dir"));

        /**
         * 所有commit和blob文件储存的地方
         */
        final static File GITLET_DIR = new File(CWD, ".gitlet");

        /**
         * commits文件夹
         */
        public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

        /** key: 文件名,  value: blob 的 uid*/
        private Map<String, String> trackedFiles;

        /**message 代表commit时候写的备注*/
        String message;

        /**uid  是一段特殊的id我们赋予的.*/
        private String uid;

        /**timeStamp是我们写的时间戳*/
        Date timeStamp;

        /**Commits的父类集合*/
        ArrayList<String> parents;


        //构造器
        public Commit(String message, String parentUid,
                      Map<String, String> AddStagingArea,
                      Map<String, String> RemoveStagingArea) {

            this.message = message;
            this.parents = new ArrayList<>();

            this.trackedFiles = new HashMap<>(); // 重要：初始化！


            // 处理父提交
            if (parentUid != null) {
                parents.add(parentUid);
                // 继承父提交的文件跟踪状态
                Commit parentCommit = Repository.getCommit(parentUid);

                if (parentCommit != null) {
                    //获取父类的TrackedFiles列表
                    this.trackedFiles.putAll(parentCommit.getTrackedFiles());
                }
            }

            // 只设置一次时间戳
            this.timeStamp = new Date();

            // 应用添加暂存区的更改（覆盖或新增文件）
            if (AddStagingArea != null) {
                for (String filename : AddStagingArea.keySet()) {
                    String blobHash = AddStagingArea.get(filename);
                    if (blobHash != null) {
                        trackedFiles.put(filename, blobHash);
                    }
                }
            }

            // 应用删除暂存区的更改
            if (RemoveStagingArea != null) {
                for (String filename : RemoveStagingArea.keySet()) {
                    trackedFiles.remove(filename);
                }
            }

            setUid(); // 生成提交ID
        }

        public Commit(Commit currentBranch, Commit givenBranch, String message){

        }

        public void setTimeStamp(Date timeStamp) {
            this.timeStamp = timeStamp;
        }

        /** Set Uid by sha1 */
        public void setUid() {
            uid=Utils.sha1(Utils.serialize(this));
        }


        public String getUid(){
            return this.uid;
        }

        /**把commit对象储存到gitletDir文件夹里*/
        public void saveCommit() {
            if (!COMMITS_DIR.exists()) {
                throw new RuntimeException("COMMITS_DIR directory does not exist");
            }
            File file = new File(COMMITS_DIR,  uid);
            //往文件里写入序列化好的对象
            Utils.writeObject(file, this);
        }

        //通过commit获取对应的TrackedFiles
        public Map <String,String> getTrackedFiles(){
            return this.trackedFiles;
        }

        /* TODO: fill in the rest of this class. */

    }