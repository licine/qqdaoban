package cat.tom.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author shkstart
 * @create 2023-11-15 1:05
 * 该类用于管理和客户端通信达线程
 */
public class ManageClientThreads {

    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    // 返回线程集合
    public static HashMap<String, ServerConnectClientThread> getHm(){
        return hm;
    }

    // 添加线程对象到hm 集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread){
        hm.put(userId, serverConnectClientThread);
    }

    // 根据userId 获取 serverConnectClientThread 线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hm.get(userId);
    }

    // 从集合中移除一个线程对象
    public static void removeServerConnectClientThread(String userId){
        hm.remove(userId);
    }

    // 返回在线用户列表
    public static String getOnlineUsers(){
        // 遍历集合
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()){
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }
}
