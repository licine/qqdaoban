package cat.tom.qqclient.service;

import cat.tom.qqcommon.Message;
import cat.tom.qqcommon.MessageType;
import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.*;

/**
 * @author shkstart
 * @create 2023-12-01 15:46
 * 该类用于完成 文件传输服务
 */
public class FileClientService {

    /**
     *
     * @param src 源文件路径
     * @param dest 把文件传输到对方的哪个目录
     * @param senderId 发送者Id
     * @param getterId 接收者Id
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId){

        // 读取src 文件，封装到message 对象中；
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSrc(src);
        message.setDest(dest);
        message.setSender(senderId);
        message.setGetter(getterId);

        // 将磁盘上的文件读入到字节数组中
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes); // 将文件读入到程序的字节数组
            // 将文件对应的字节数组设置message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println("\n" + senderId + " 给 " + getterId + "发送文件: " + src + "到对方目录: " + dest);

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
