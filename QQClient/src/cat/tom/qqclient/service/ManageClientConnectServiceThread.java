package cat.tom.qqclient.service;

import javax.crypto.Cipher;
import java.util.HashMap;

/**
 * @author shkstart
 * @create 2023-11-10 11:15
 * 该类管理客户端连接到服务器端的线程的类
 */
public class ManageClientConnectServiceThread {

    // 我们把多个线程放入一个HashMap 集合，key 就是用户 id，value 就是线程；
    public static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    // 将某个线程加入集合
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread){
        hm.put(userId, clientConnectServerThread);
    }

    // 通过userId 可以得到对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }

}
