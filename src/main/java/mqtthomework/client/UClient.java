package mqtthomework.client;

import com.google.gson.Gson;
import mqtthomework.callback.ClientCallBack;
import mqtthomework.entity.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UClient {

    private String UClientName;

    // 昵称作为该mqtt客户端的name
    public UClient(String UClientName){
        this.UClientName = UClientName;
    }

    public String getUClientName() {
        return UClientName;
    }

    // 创建一个MQTT连接
    public MqttClient getMqttClient(String connectionClientId) throws MqttException {
        // broker的地址
        String broker = "tcp://127.0.0.1:1883";
        // 先用固定值，不同的客户端这个属性也不同(这里默认使用clientId,由用户输入，跟MqttClientName同值)
        String clientId = connectionClientId;
        MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
        // 创建链接参数
        MqttConnectOptions connOpts = new MqttConnectOptions();
        // 在重新启动和重新连接时记住状态
        connOpts.setCleanSession(false);
        // 建立连接
        mqttClient.connect(connOpts);
        return mqttClient;
    }

    public static void showMenu(MqttClient mqttClient,String clientId) throws Exception {
        while(true){
            System.out.println("当前你要做的操作：");
            System.out.println("1、登录");
            System.out.println("2、注册");
            System.out.println("3、登出");
            System.out.println("4、创建兼职");
            System.out.println("5、查看兼职");
            System.out.println("6、加入兼职");
            System.out.println("7、兼职通知/回复");
            System.out.println("8、退出兼职");
            Scanner scanner = new Scanner(System.in);
            int choose = scanner.nextInt();
            switch (choose){
                case 1: login(mqttClient,clientId);break;
                case 2: register(mqttClient);break;
                case 3: quitLogin(mqttClient,clientId);break;
                case 4: createPartTime(mqttClient,clientId);break;
                case 5: lookPartTime(mqttClient,clientId); break;
                case 6: joinPartTime(mqttClient,clientId);break;
                case 7: resever(mqttClient,clientId);break;
                case 8: quitPartTime(mqttClient,clientId);break;
                default:break;
            }
        }
    }

    // 登出
    private static void quitLogin(final MqttClient mqttClient, final String clientId) throws MqttException {
        MqttMessage message = new MqttMessage(clientId.getBytes());
        message.setQos(2);
        mqttClient.publish("U/"+clientId+"/quitLogin",message);
        new Thread(new Runnable() {
            public void run() {
                try {
                    mqttClient.subscribe("M/"+clientId+"/#", 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 退出兼职
    private static void quitPartTime(final MqttClient mqttClient, String clientId) throws MqttException {
        System.out.println("请输入要退出的兼职：");
        Scanner scanner = new Scanner(System.in);
        int quitId = scanner.nextInt();
        QuiterAndId quiterAndId = new QuiterAndId(clientId,quitId);
        Gson gson = new Gson();
        String quiterAndIdJson = gson.toJson(quiterAndId);
        MqttMessage message = new MqttMessage(quiterAndIdJson.getBytes());
        message.setQos(2);
        mqttClient.publish("E/quitPart/quitPartTime",message);
        new Thread(new Runnable() {
            public void run() {
                try {
                    mqttClient.subscribe("M/quit/#", 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    // 信息发布和回复
    private static void resever(final MqttClient mqttClient, final String clientId) throws MqttException {
        // 先传入clientId判断他创建的兼职是哪一个
        System.out.println("请输入要传话的兼职ID：");
        Scanner scanner = new Scanner(System.in);
        int i = scanner.nextInt();
        CreaterAndId createrAndId = new CreaterAndId(clientId,i);
        Gson gson = new Gson();
        String createrAndIdJson = gson.toJson(createrAndId);
        MqttMessage message = new MqttMessage(createrAndIdJson.getBytes());
        message.setQos(2);
        mqttClient.publish("E/checkPartTime/checking",message);
        mqttClient.subscribe("M/"+clientId+"/#", 2);
        /*new Thread(new Runnable() {
            public void run() {
                try {
                    mqttClient.subscribe("M/"+clientId+"/#", 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    // 加入兼职
    private static void joinPartTime(final MqttClient mqttClient, final String clientId) throws MqttException {
        System.out.println("请输入要加入的兼职编号(id):");
        Scanner scanner = new Scanner(System.in);
        int choosePartTime = scanner.nextInt();
        JoinInfo joinInfo = new JoinInfo(choosePartTime,clientId,"110112111","晚点到");
        Gson gson = new Gson();
        String jonInfoJson = gson.toJson(joinInfo);
        MqttMessage message = new MqttMessage(jonInfoJson.getBytes());
        message.setQos(2);
        mqttClient.publish("E/join/user", message);
        new Thread(new Runnable() {
            public void run() {
                try {
                    mqttClient.subscribe("U/"+clientId+"/EPjoin", 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 查看兼职
    private static void lookPartTime(final MqttClient mqttClient, final String clientId) throws MqttException {
        MqttMessage message = new MqttMessage(clientId.getBytes());
        message.setQos(2);
        mqttClient.publish("E/list/user", message);
        new Thread(new Runnable() {
            public void run() {
                try {
                    mqttClient.subscribe("M/"+clientId+"/Elist", 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 创建兼职（先判断该client是否已经登录，根据回调结果看是否具备创建兼职的条件）
    private static void createPartTime(final MqttClient mqttClient, final String clientId) throws MqttException {
            // 封装成MQTT可发送的执行流
            MqttMessage message = new MqttMessage(clientId.getBytes());
            message.setQos(2);
            mqttClient.publish("M/isLogin/Login", message);
            // 开辟一个线程去订阅信息，等待数据
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mqttClient.subscribe("U/"+clientId+"/isLogin", 2);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

    }


    // 登录
    private static void login(final MqttClient mqttClient, final String clientId) throws MqttException {
        // 封装成JSON
        Gson gson = new Gson();
        String username = getInfo("请输入账号：");
        String password = getInfo("请输入密码：");
        LoginEntity loginEntity = new LoginEntity(clientId,username,password);
        String loginJson = gson.toJson(loginEntity);
        // 封装成MQTT可发送的执行流
        MqttMessage message = new MqttMessage(loginJson.getBytes());
        message.setQos(2);
        mqttClient.publish("M/Ulog/Ulog",message);
        System.out.println(clientId);
        // 开辟一个线程去订阅信息，等待数据
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 订阅对应ID
                    mqttClient.subscribe("U/"+clientId + "/MUreg",2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 获取本机Ip
    private static String getLocalIp() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String[] split = localHost.toString().split("/");
        return split[1];
    }

    // 封装输入数据
    private static String getInfo(String message){
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        String info = scanner.next();
        return info;
    }

    // 用户注册
    private static void register(final MqttClient mqttClient) throws Exception {
        String localIp = getLocalIp();
        String info = "M/Ureg/"+localIp;
        // 封装成JSON
        Gson gson = new Gson();
        String clientId = getInfo("请输入ClientId：");
        String username = getInfo("请输入账号：");
        String password = getInfo("请输入密码：");
        LoginEntity loginEntity = new LoginEntity(clientId,username,password);
        String loginJson = gson.toJson(loginEntity);
        // 封装成MQTT可发送的执行流
        MqttMessage message = new MqttMessage(loginJson.getBytes());
        message.setQos(2);
        mqttClient.publish(info,message);
        // 开辟一个线程去订阅信息，等待数据
        new Thread(new Runnable() {
            public void run() {
                try {
                    mqttClient.subscribe("U/#",2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        String clientId = getInfo("请输入账号昵称（ClientId）:");
        UClient uClient = new UClient(clientId);
        MqttClient mqttClientEntity = uClient.getMqttClient(clientId);
        mqttClientEntity.setCallback(new ClientCallBack(mqttClientEntity,uClient.getUClientName()));
        showMenu(mqttClientEntity,uClient.getUClientName());
        mqttClientEntity.disconnect(); // 断开连接
        mqttClientEntity.close(); // 关闭客户端
    }

}
