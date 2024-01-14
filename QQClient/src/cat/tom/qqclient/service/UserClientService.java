package cat.tom.qqclient.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;
import cat.tom.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

/**
 * @author shkstart
 * @create 2023-11-10 10:41
 */
public class UserClientService {

    // 因为我们可能在其他地方使用user 信息，因此做成成员属性
    private User u = new User();

    // 因为socket 在其他地方也可能使用，因此做成属性
    Socket socket;

    // 根据userId 和 pwd 到服务器验证该用户是否合法
    public boolean checkUser(String userId, String pwd){
        boolean b = false;
        // 创建user 对象
        u.setUserId(userId);
        u.setPasswd(pwd);

        // 连接到服务器， 发送u 对象
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u); // 发送User 对象

            // 读取从服务器回复的 Message 对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message)ois.readObject();
            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)){ // 登陆成功

                // 创建一个和服务器保持通信达线程 --> 创建一个类 ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                // 启动客户端线程
                clientConnectServerThread.start();
                // 这里为了后面客户端的扩展，我们将线程放入集合管理
                ManageClientConnectServiceThread.addClientConnectServerThread(userId, clientConnectServerThread);

                b = true;
            } else { // 登陆失败, 我们就不能启动和服务器通信的线程，关闭socket
                socket.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return b;
    }

    // 向服务器端请求用户列表
    public void onlineFriendList(){
        // 发送一个Message 类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        // 发送给服务器
        // 拿到当前线程的socket 的输入流
        try {
            // 有多个socket 的话还是不能这样写；
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectOutputStream oos =
              new ObjectOutputStream(ManageClientConnectServiceThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message); // 向服务器发送一个Message 对象，向服务器请求在线用户列表
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    // 退出客户端并给服务端发送一个退出系统的message 对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId()); // 一定要指定哪个客户端要关闭

        // 发送message
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServiceThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + "退出");
            System.exit(0); // 结束进程
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
