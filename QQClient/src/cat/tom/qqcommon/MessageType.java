package cat.tom.qqcommon;

/**
 * @author shkstart
 * @create 2023-11-07 1:42
 * 表示消息类型
 */
public interface MessageType {

    String MESSAGE_LOGIN_SUCCESS = "1"; // 表示登陆成功
    String MESSAGE_LOGIN_FAIL = "2"; // 表示登陆失败
    String MESSAGE_COMM_MES = "3"; // 普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4"; // 要求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5"; // 返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6"; // 客户端请求退出
    String MESSAGE_TO_ALL_MES = "7"; // 群发消息
    String MESSAGE_FILE_MES = "8"; // 群发消息

}
