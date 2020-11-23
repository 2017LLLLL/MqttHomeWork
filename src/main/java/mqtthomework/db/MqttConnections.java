package mqtthomework.db;

import mqtthomework.entity.LoginEntity;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @PackageName mqtthomework.db
 * @Classname MqttConnections
 * @Description 用于存储MQTT登录队列 和 用户账号
 * @Date 2020/9/5 19:48
 * @Author flj
 * @Version 1.0.0
 */
public class MqttConnections {

    // 存放登录队列（clientId队列）
    public static ArrayList<String> MqttLoginedList = new ArrayList<String>();

    // 存放登录账号密码用户名（clientId）的队列
    public static ArrayList<HashMap<String, String>> UserList = new ArrayList<HashMap<String, String>>();


    static {
        // 创建10个基础账号
        for (int i = 0; i <10 ; i++) {
            HashMap map = new HashMap();
            map.put("username","test"+i);
            map.put("password","123456");
            map.put("clientId","client"+i);
            UserList.add(map);
        }
    }


    // 校验账号和clientId是否存在
    public static HashMap checkIsExist(String username,String clientId){
        for (int i = 0; i < UserList.size() ; i++) {
            HashMap<String, String> UserLoginMap = UserList.get(i);
            if(UserLoginMap.get("username").equals(username) && UserLoginMap.get("clientId").equals(clientId)){
                return UserLoginMap;
            }
        }
        return null;
    }

    // 注册
    public static void register(LoginEntity loginEntity){
        HashMap map = new HashMap();
        map.put("username",loginEntity.getUsername());
        map.put("password",loginEntity.getPassword());
        map.put("clientId",loginEntity.getClientId());
        UserList.add(map);
    }

    // 校验账号是否存在
    public static HashMap checkIsExist(String username){
        for (int i = 0; i < UserList.size() ; i++) {
            HashMap<String, String> UserLoginMap = UserList.get(i);
            if(UserLoginMap.get("username").equals(username)){
                return UserLoginMap;
            }
        }
        return null;
    }


    // 检查账号密码是否正确
    public static boolean checkIsLoginInfo(LoginEntity loginEntity){
        HashMap UserLoginMap = checkIsExist(loginEntity.getUsername());
        if(UserLoginMap == null){
            return false;
        }

        // 获取该账号对应的密码
        String DBPassword = (String)UserLoginMap.get("password");
        String DBClientId = (String)UserLoginMap.get("clientId");
        if(DBPassword.equals(loginEntity.getPassword())&& DBClientId.equals(loginEntity.getClientId())){
            return true;
        }else {
            return false;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i <UserList.size(); i++) {
            System.out.println(UserList.get(i));
        }

    }


}
