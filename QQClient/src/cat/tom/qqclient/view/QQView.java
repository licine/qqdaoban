package cat.tom.qqclient.view;

import cat.tom.qqclient.service.FileClientService;
import cat.tom.qqclient.service.ManageClientConnectServiceThread;
import cat.tom.qqclient.service.MessageClientService;
import cat.tom.qqclient.service.UserClientService;
import cat.tom.qqclient.utils.Utility;

import java.lang.invoke.VarHandle;

/**
 * @author shkstart
 * @create 2023-11-08 0:27
 */
public class QQView {

    private boolean loop = true; // 控制是否显示菜单
    private String key = ""; // 接收用户输入

    private MessageClientService messageClientService = new MessageClientService(); // 发送消息

    private UserClientService userClientService = new UserClientService(); // 用于登录服务/注册用户；

    private FileClientService fileClientService = new FileClientService(); // 用于文件传输

    public static void main(String[] args) {

        new QQView().mainMenu();
        System.out.println("客户端退出系统。。。。。。");

    }

    // 显示主菜单
    private void mainMenu(){
        while (loop){
            System.out.println("\n=====================欢迎登录网络通信系统=====================");
            System.out.println("\t\t1 登录系统");
            System.out.println("\t\t9 退出系统");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);
            switch (key){
                // 根据用户的输入来处理不同的逻辑
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码：");
                    String passwd = Utility.readString(50);
                    // 这里比较麻烦，需要到服务器端进行验证，
                    // 这里有很多代码，我们这里编写一个类 UserClientService【用户登录/注册】
                    if (userClientService.checkUser(userId, passwd)){ // 这里还没写完，先把整个逻辑打通
                        while (loop){
                            System.out.println("\n=====================网络通信系统二级菜单（用户 " + userId + " ）=====================");
                            System.out.println("\t\t1 显示在线用户列表");
                            System.out.println("\t\t2 群发消息");
                            System.out.println("\t\t3 私聊消息");
                            System.out.println("\t\t4 发送文件");
                            System.out.println("\t\t9 退出系统");
                            System.out.print("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    // 编写一个方法用于显示用户列表
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.print("请输入你想要对大家说的话：");
                                    String s = Utility.readString(100);
                                    // 发送消息
                                    messageClientService.sendMessageToAll(userId, s);
                                    break;
                                case "3":
                                    System.out.print("请输入你想要私聊的人：");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入你要说的话：");
                                    String content = Utility.readString(100);
                                    // 发送消息
                                    messageClientService.sendMessageToOne(userId, getterId, content);
                                    break;
                                case "4":
                                    System.out.print("请输入你想把文件发给谁：");
                                    getterId = Utility.readString(50);
                                    System.out.print("请输入发送文件的路径（形式：d:\\xx.jpg）：");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入把文件发送到对应的路径（形式：d:\\xx.jpg）：");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src,dest,userId,getterId);
                                    break;
                                case "9":
                                    // 调用方法，给服务器端发送一个退出系统的message；
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }

                        }
                    } else {
                        System.out.println("用户名或密码错误，请重新登录！");
                        break;
                    }
                    break;
                case "9":
                    System.out.println("=====================退出网络通信系统=====================\n");
                    loop = false;
                    break;
            }

        }
    }


}
