package cat.tom.qqcommon;

import java.io.Serializable;

/**
 * @author shkstart
 * @create 2023-11-07 1:33
 * 表示一个用户/客户的信息
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userId; // 用户id
    private String passwd; // 用户密码

    public User() {
    }

    public User(String userId, String passwd) {
        this.userId = userId;
        this.passwd = passwd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
