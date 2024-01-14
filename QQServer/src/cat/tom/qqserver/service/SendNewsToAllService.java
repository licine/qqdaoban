package cat.tom.qqserver.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;
import cat.tom.qqserver.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author shkstart
 * @create 2023-12-06 0:45
 */
public class SendNewsToAllService implements Runnable{
    @Override
    public void run() {

        // 使用while 循环可以多次推送
        while(true){
            System.out.println("请输入要推送的新闻/消息【输入exit 推出推送服务线程】");
            String news = Utility.readString(100);
            if ("exit".equals(news)){
                break;
            }
            // 构建一个消息，群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人说：" + news);

            // 遍历当前所有通信线程，得到socket，并群发 message
            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()){
                String onlineUserId = iterator.next().toString();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }


    }
}
