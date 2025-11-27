package gitlet;

import java.util.*;

public class WodeTest2 {
    public static void main(String[] args) {
        //我想要一个列表 数组集合
        LinkedList<String> queue= new LinkedList<>();

        //FIFO LinkedList运用了Queue的接口
        queue.offer("current");
        queue.offer("next");
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }
}
