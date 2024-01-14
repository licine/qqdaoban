package cat.tom.qqserver.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;
import sun.nio.cs.ext.MS874;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * @author shkstart
 * @create 2023-11-15 0:53
 * 该类的一个对象和一个客户端保持通信
 */
public class ServerConnectClientThread extends Thread{

    private Socket socket;
    private String userId; // 连接到服务端的用户id；

    public ServerConnectClientThread(Socket socket, String userId){

        this.socket = socket;
        this.userId = userId;

    }

    @Override
    public void run() { // 这里线程处于 run 的状态，可以发送/接收消息
        while (true){
            System.out.println("服务端和客户端" + userId + "保持通信，读取数据。。。。");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)ois.readObject();
                // 判断是否是客户端要用户列表
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    // 发送在线用户列表
                    System.out.println(message.getSender() + "要用户列表");
                    // 从管理线程的集合中拿到在线用户列表
                    String onlineUsers = ManageClientThreads.getOnlineUsers();
                    // 构建一个Message 对象返回给客户端
                    Message message1 = new Message();
                    message1.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message1.setContent(onlineUsers);
                    message1.setGetter(message.getSender());
                    // 发送给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message1);


                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){ // 发送消息
                    if (ManageClientThreads.getServerConnectClientThread(message.getGetter()) != null){    // 好友在线[从线程集合中判断]
                        Socket userSocket = ManageClientThreads.getServerConnectClientThread(message.getGetter()).getSocket();
                        // 将消息发送给要发送给的人
                        ObjectOutputStream oos =
                                new ObjectOutputStream(userSocket.getOutputStream());
                        oos.writeObject(message); //转发，如果客户不存在，可以保存到数据库，这样就可以实现离线留言
                    } else {    // 不在线, 将message保存到数据库（ArrayList） ）
                        QQServer.addMessageToOfflineDb(message.getGetter(),message);
                    }
                    
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){ // 发送消息
                    // 遍历线程集合，拿到socket 发送给所有人
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()){
                        // 拿到用户id；
                        String onlineUserId = iterator.next().toString();
                        // 排除自己
                        if (!onlineUserId.equals(message.getSender())){

                            ObjectOutputStream oos =
                                    new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);

                        }
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) { // 文件传输
                    if (ManageClientThreads.getServerConnectClientThread(message.getGetter()) != null) {    // 好友在线[从线程集合中判断]
                        // 根据getter id 拿到相应的线程，将message 转发
                        ObjectOutputStream oos =
                                new ObjectOutputStream(ManageClientThreads.getServerConnectClientThread(message.getGetter()).getSocket().getOutputStream());

                        // 转发
                        oos.writeObject(message);
                    } else {    // 不在线, 将message保存到数据库（ArrayList） ）
                        QQServer.addMessageToOfflineDb(message.getGetter(),message);
                    }


                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) { // 客户端退出
                    System.out.println(message.getSender() + "退出");
                    // 将这个连接客户端的线程从集合中移除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());
                    socket.close(); // 关闭连接
                    break; // 退出线程；如果没有这个break 它还跑到35 行去读，就会有很多io 异常；
                } else {
                    System.out.println("其他类型的message，暂不处理");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket getSocket(){
        return socket;
    }
}
