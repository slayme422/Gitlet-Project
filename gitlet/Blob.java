package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.join;

public class Blob implements Serializable {

   //Current working directory
   static File cwd=new File(System.getProperty("user.dir"));

   //Gitlet repository root directory
   final static File GITLET_DIR =new File(cwd,".gitlet");

   public static final File COMMITS_DIR =join(GITLET_DIR,"commits");

   //Directory of store blobs
   public static final File BLOBS_DIR=join(GITLET_DIR,"blobs");

   static Map<String,Blob> blobs =new HashMap<String,Blob>();

   String content;

   String uid;


    public Blob(File file){
        content=Utils.readContentsAsString(file);
        setUid();
    }
    //返回一串sha-1计算的uid
    public void setUid(){
        uid=Utils.sha1(Utils.serialize(this));
        blobs.put(uid,this);
    }

    //在当前工作区域里创建一个文件夹,name作为文件名
    public void saveToFile(){
        if (!BLOBS_DIR.exists()) BLOBS_DIR.mkdir();
        File file=new File(BLOBS_DIR,uid);
        Utils.writeObject(file,this);//write obj to file!
    }

    //读取这个UID来获得当前文件，返回Apple对象
    public static Blob readFile(String uid){
        File file=new File(BLOBS_DIR,uid);
        return Utils.readObject(file, Blob.class);
    }

    /**
     * Return the content of the blob
     *
     * @Return the blob's content as a String
     */
    public String getContent(){
        return this.content;
    }

}
