package cat.tom.qqserver.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;
import cat.tom.qqcommon.User;

import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

/**
 * @author shkstart
 * @create 2023-11-15 0:34
 */
public class QQServer {

    private ServerSocket ss = null;

    //创建一个集合，存放多个用户，如果是这些用户登录，就认为是合法的
    //HashMap 没有处理线程安全，因此在多线程下是不安全的
    // ConcurrentHashMap 处理的线程安全，即线程同步处理，在多线程下是安全的
    private static ConcurrentHashMap<String, User> validUser = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineDb = new ConcurrentHashMap<>();
    private static ArrayList<Message> user100Db;
    private static ArrayList<Message> user200Db;

    static { // 在静态代码块初始化 validUser

        validUser.put("100", new User("100", "123456"));
        validUser.put("200", new User("200", "123456"));
        validUser.put("300", new User("300", "123456"));
        validUser.put("至尊宝", new User("至尊宝", "123456"));
        validUser.put("紫霞仙子", new User("紫霞仙子", "123456"));
        validUser.put("菩提老祖", new User("菩提老祖", "123456"));
        user100Db = new ArrayList<>();
        user200Db = new ArrayList<>();
        offlineDb.put("100", user100Db);
        offlineDb.put("200", user200Db);

    }

    // 向外暴露一个方法，向离线数据库中添加消息
    public static void addMessageToOfflineDb(String userId, Message message){
        if (userId.equals("100")){
            user100Db.add(message);
        }
        if (userId.equals("200")){
            user200Db.add(message);
        }
    }

    // 清空ArrayList 和 移除已发送用户的offlineDb
    public void clearOffLineDb(String userId){
        if (userId.equals("100")){
            user100Db.clear();
        }
        if (userId.equals("200")){
            user200Db.clear();
        }
        // 该代码会导致退出系统后再登录无法接受离线消息；仅仅情况arraylist即可；
//        offlineDb.remove(userId);
    }

    // 验证用户是否合法
    public boolean checkUser(String userId, String passwd){
        User user = validUser.get(userId);
        // 说明userId 没有存放在 validUser中；
        if (user == null){
            return false;
        }
        // 验证密码是否正确
        return user.getPasswd().equals(passwd);
    }

    // 验证用户是否有离线消息或文件，并发送；
    public void haveOffLineMessage(String userId, Socket socket){
        ArrayList<Message> offlineMessageList = offlineDb.get(userId);
        ObjectOutputStream oos = null;
        if (!offlineMessageList.isEmpty()){
            for (Message mess : offlineMessageList){
                try {
                    // 每次循环都要new 一个 oos
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(mess);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            // 清空Db
            clearOffLineDb(userId);
        }

    }

    public QQServer(){
        System.out.println("服务端在9999端口监听…………");
        // 启动推送线程服务
        new Thread(new SendNewsToAllService()).start();
        try {
            // 端口可以写在配置文件
            ss = new ServerSocket(9999);

            while (true){   //当和某个客户端建立连接后会继续监听，因此用while
                Socket socket = ss.accept();
                // 得到socket 关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                // 得到socket 关联的对象输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User)ois.readObject(); // 读取客户端发送到User 对象；
                // 创建一个Message 对象，准备回复客户端
                Message message = new Message();
                // 验证
                if (checkUser(u.getUserId(), u.getPasswd())){ // 登录成功

                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCESS);

                    // 将message 对象回复客户端
                    oos.writeObject(message);

                    // 创建一个线程，和客户端保持通信，该线程需要持有socket 对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, u.getUserId());
                    serverConnectClientThread.start(); // 启动线程
                    // 把该线程对象放到一个集合中进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);
                    // 看该用户是否有离线消息或文件, 并发送；
                    haveOffLineMessage(u.getUserId(), socket);

                } else { // 登陆失败
                    System.out.println("用户userId = " + u.getUserId() + " 密码pwd = " + u.getPasswd() + " 的用户登陆失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    // 关闭socket
                    socket.close();

                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 如果服务器推出了while 循环，说明服务器端不在监听，因此关闭ServerSocket
            try {
                ss.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
