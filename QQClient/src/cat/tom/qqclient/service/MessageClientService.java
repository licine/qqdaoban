package cat.tom.qqclient.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author shkstart
 * @create 2023-11-21 1:31
 */
public class MessageClientService {


    /**
     *
     * @param senderId 发送者
     * @param content 发送者对大家说的话
     */
    public void sendMessageToAll(String senderId, String content){
        // 封装成消息，发送给服务端
        Message message = new Message();
        message.setSender(senderId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSendTime(new Date().toString());
        System.out.println(senderId + "对大家说" + content);
        // 发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServiceThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param senderId 发送者
     * @param getterId 接收者
     * @param content 发送内容
     */
    public void sendMessageToOne(String senderId, String getterId, String content){
        // 封装成消息，发送给服务端
        Message message = new Message();
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSendTime(new Date().toString());
        System.out.println(senderId + "对" + getterId + "说" + content);
        // 发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServiceThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
