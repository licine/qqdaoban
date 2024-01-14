package cat.tom.qqclient.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;

import javax.naming.ldap.SortResponseControl;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * @author shkstart
 * @create 2023-11-10 10:59
 */
public class ClientConnectServerThread extends Thread{

    // 该线程必须持有socket
    private Socket socket;

    // 构造器可以接受一个Socket 对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        // 因为Thread 需要在后台和服务器通信，因此使用while 循环
        while (true){
            System.out.println("客户端线程，等待读取从服务器端发送到消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务器没有发送Message 对象，线程会阻塞在这里
                Message ms = (Message) ois.readObject();
                // 我们后面再使用这个ms ，比如把消息显示在控制台
                // 如果读取到的是服务端返回的用户列表
                if (Objects.equals(ms.getMesType(), MessageType.MESSAGE_RET_ONLINE_FRIEND)){

                    // 取出在线用户列表进行展示，
                    // 发送到用户列表信息规定用空格间隔，这样好分割
                    String[] onlineUsers = ms.getContent().split(" ");
                    System.out.println("\n==================当前在线用户列表==================");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }
                } else if (Objects.equals(ms.getMesType(), MessageType.MESSAGE_COMM_MES)){ // 普通消息
                    System.out.println("\n" + ms.getSender() + "对" + ms.getGetter() + "说：" + ms.getContent());

                } else if (Objects.equals(ms.getMesType(), MessageType.MESSAGE_TO_ALL_MES)){ // 群发消息
                    System.out.println("\n" + ms.getSender() + "对大家说：" + ms.getContent());

                } else if (ms.getMesType().equals(MessageType.MESSAGE_FILE_MES)){ // 发送文件

                    // 取出message 的文件字节数组，通过文件输出流写到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(ms.getDest());
                    fileOutputStream.write(ms.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n" + ms.getSender() + " 给 " + ms.getGetter() + "发送文件：" + ms.getSrc() + "到：" +
                            ms.getDest());
                    System.out.println("保存文件成功~");


                }
                else {
                    System.out.println("是其他类型的message，无法展示");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    // 为了方便得到socket
    public Socket getSocket(){
        return socket;
    }


}
