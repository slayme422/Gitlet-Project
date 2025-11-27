package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Stamp implements Serializable {
    //当前路径
    File pwd=new File(System.getProperty("user.dir"));

    private Date time;

    private String uid;

    HashMap<String,File> hm=new HashMap<>();

    public Stamp(){
        time=new Date (System.currentTimeMillis());
        setUid();
    }

    public void setUid() {
        this.uid = Utils.sha1(Utils.serialize(this));
    }
    //如何保存到电脑上
    public void saveFile(){
        //创建一个新File
        File file=new File(pwd,uid);
        Utils.writeObject(file,this);
        hm.put(uid,file);
    }
    public void readFile(String uid){
        File file=new File(pwd,uid);
        //从当前硬盘读取那个文件用uid加上一个.txt
        Utils.readObject(Utils.join(pwd,"uid"),Stamp.class);
    }
    public static void main(String[] args) {
        Stamp st1=new Stamp();

        System.out.println(st1.time);
        st1.saveFile();

        
    }
}
