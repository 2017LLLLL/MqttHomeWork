package mqtthomework.callback;

import com.google.gson.Gson;
import mqtthomework.db.MqttConnections;
import mqtthomework.entity.PartTimeEntity;
import mqtthomework.utils.ClientCaoZuo;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @PackageName mqtthomework.db
 * @Classname ClientCallBack
 * @Description 客户端回调函数，用于接收来自服务端的消息推送
 * @Author flj
 * @Version 1.0.0
 */
public class ClientCallBack implements MqttCallback {

    private MqttClient mqttClient;

    private String mqttClientName;

    public ClientCallBack(MqttClient mqttClient, String mqttClientName){
        this.mqttClient = mqttClient;
        this.mqttClientName = mqttClientName;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 掉线就死了，暂时不处理
        System.out.println("掉线了，请重新启动连接......");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String content = new String(message.getPayload());
        System.out.println("topic:"+topic);
        System.out.println("Qos:"+message.getQos());
        System.out.println("收到的消息:"+content);
        doing(topic,content);
    }



    // 选择执行的操作
    private void doing(String topic, final String message) throws MqttException {
        String[] split = topic.split("/");
        String whatToDo = split[2];
        int code = ClientCaoZuo.getCode(whatToDo);
        // 如果是登录返回的消息
        if (code == 1) {
            showLoginInfo(message);
        } else if (code == 2) {
            // 成功创建兼职
            successCreatePartTime(message);
        } else if (code == 3 && split[0].equals("U")) {
            // 成功加入兼职
            successJoinPartTime(message);
        } else if (code == 4) {
            // 检查是否处于登录状态
            checkIsLogined(message);
        } else if (code == 5) {
            // 注册情况
            registerRelation(message);
        } else if (code == 6) {
            // 检查该用户是否有创建兼职
            checkCreatePartTime(message);
        } else if(code == 7){
            quitSuccess(message);
        }else if(code == 8){
            // 登出
            loginOut(message);
        }else if(code ==9){
            // 查看兼职列表
            partTimeList(message);
        }else if(code == 10){
            listen(message);
        }

    }

    // 收到监听的消息
    private void listen(String message) {
        System.out.println("创建者说："+message);
    }

    // 兼职列表
    private void partTimeList(String message) throws MqttException {
        System.out.println("当前所有兼职："+message);
    }

    //登出
    private void loginOut(String message) throws MqttException {
        if("success".equals(message)){
            System.out.println("登出成功！");
            this.mqttClient.unsubscribe("M/quit/quitPart");
            this.mqttClient.unsubscribe("E/#");
        }else{
            System.out.println("登出失败，原因"+message);
        }
    }


    // 退出兼职成功
    private void quitSuccess(String message) {
        if("success".equals(message)){
            System.out.println("退出成功！");
        }
    }

    // 检查是否有创建兼职
    private void checkCreatePartTime(String message) throws MqttException {
        if ("falie".equals(message)) {
            System.out.println("该用户为创建任何兼职");
        } else {
            while(true) {
                System.out.println("请输入要留言的话：");
                Scanner scanner = new Scanner(System.in);
                String next = scanner.next();
                if(next.equals("exit")){
                    break;
                }
                System.out.println("留言内容：" + next);
                MqttMessage mqttMessage = new MqttMessage(next.getBytes());
                mqttMessage.setQos(2);
                this.mqttClient.publish("E/" + message + "/listen", mqttMessage);
            }
            this.mqttClient.unsubscribe("M/"+this.mqttClientName+"/check");
        }
    }

    // 注册情况
    private void registerRelation(String message) throws MqttException {
        if ("success".equals(message)) {
            System.out.println("注册成功！");
        } else {
            System.out.println("注册失败，原因：" + message);
        }
        // 不管成功与否都取消订阅
        this.mqttClient.unsubscribe("U/#");
    }



    // 检查是否处于登录
    private void checkIsLogined(String message) throws MqttException {
        if ("logined".equals(message)) {
            // 封装成JSON
            System.out.println("用户已登录，正在创建兼职，请联系管理员审批！");
            Gson gson = new Gson();
            final PartTimeEntity partTimeEntity = new PartTimeEntity(new Date(), 10, "wuxing", this.mqttClientName);
            String partTimeJson = gson.toJson(partTimeEntity);
            // 封装成MQTT可发送的执行流
            MqttMessage sendMessage = new MqttMessage(partTimeJson.getBytes());
            sendMessage.setQos(2);
            mqttClient.publish("M/Prelease/柳州XX公司", sendMessage);
            // 开辟一个线程去订阅信息，等待数据
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mqttClient.subscribe("E/"+partTimeEntity.getCreater()+"/MPrelease", 2);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            System.out.println(this.mqttClientName+"未登录！");
        }
    }

    // 成功加入兼职的操作
    private void successJoinPartTime(final String message) {
        if (!"falie".equals(message)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mqttClient.subscribe("E/" + message + "/listen", 2);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println("加入兼职成功，并订阅了 E/" + message +"/listen");
        } else {
            System.out.println("加入兼职失败!");
        }
    }

    // 成功创建兼职的操作
    private void successCreatePartTime(final String message) throws MqttException {
        // 直接退订
        mqttClient.unsubscribe("E/柳州XX公司/#");
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 订阅对应兼职号的ID
                    mqttClient.subscribe("E/" + message + "/talk", 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("创建兼职成功并订阅了：E/" + message + "/talk 的兼职信息");
    }

    //展示登录信息
    private void showLoginInfo(String message) {
        if ("success".equals(message)) {
            System.out.println("登录成功！");
        } else {
            System.out.println("登录失败，原因：" + message);
        }
    }

    // 不处理
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) { }
}
