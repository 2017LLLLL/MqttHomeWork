package mqtthomework.entity;



/**
 * @PackageName mqtthomework.db
 * @Classname LoginEntity
 * @Description 登录信息实体类
 * @Date 2020/9/6 19:25
 * @Author flj
 * @Version 1.0.0
 */
public class LoginEntity {

    private String clientId;
    private String username;
    private String password;




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public LoginEntity(String clientId, String username, String password) {
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LoginEntity(){

    }
}
