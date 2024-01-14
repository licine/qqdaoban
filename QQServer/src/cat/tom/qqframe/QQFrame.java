package cat.tom.qqframe;

import cat.tom.qqserver.service.QQServer;

/**
 * @author shkstart
 * @create 2023-11-15 6:05
 * 该类创建一个QQServer对象，相当于启动后台服务
 */
public class QQFrame {

    public static void main(String[] args) {
        new QQServer();
    }
}
