package mqtthomework.callback;

import com.google.gson.Gson;
import mqtthomework.db.MqttConnections;
import mqtthomework.db.PartTimeManger;
import mqtthomework.entity.*;
import mqtthomework.server.ManServer;
import mqtthomework.utils.CaoZuo;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * @PackageName mqtthomework.db
 * @Classname MessageCallBack
 * @Description 服务端回调函数，用于接收来自服务端的消息推送
 * @Author flj
 * @Version 1.0.0
 */
public class MessageCallBack implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        // 掉线就死了，暂时不处理
        System.out.println("掉线了，请重新启动连接......");
    }

    // 收到推送消息执行
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String content = new String(message.getPayload());
        System.out.println("topic:"+topic);
        System.out.println("Qos:"+message.getQos());
        System.out.println("收到的消息:"+content);
        // 根据主题和信息执行下一步的操作
        doing(topic,content);
    }

    // 推送完毕执行
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------"+ token.isComplete());
    }

    // 解析传入的主题进行分割获取
    private static void doing(String topic,String content) throws SQLException, MqttException {
        String[] split = topic.split("/");
        System.out.println(split[1]);
        // 传入主题进行id的转换
        int code = CaoZuo.getCode(split[1]);
        System.out.println(code);
        // 根据返回id处理信息
        if(code == 1){
            register(content);
        }else if(code == 2){
            login(content);
        }else if(code == 3){
            createPartTime(content);
        }else if(code == 4){
            lookPartTime(content);
        }else if(code == 5){
            joinPartTime(content);
        }else if(code ==6){
            isLogin(content);
        }else if(code ==7 && split[0].equals("E")){
            check(content);
        }else if(code == 8  && split[0].equals("E")){
            quitPartTime(content);
        }else if(split[0].equals("U") && split[2].equals("quitLogin")){
            loginout(content);
        }
    }

    // 登出
    private static void loginout(String content) throws MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        // 直接拿来对不，找不到就没有登录
        for (int i = 0; i < MqttConnections.MqttLoginedList.size() ; i++) {
            String loginClientId = MqttConnections.MqttLoginedList.get(i);
            System.out.println(loginClientId);
            if(loginClientId.equals(content)){
                System.out.println("进入到移除！");
                MqttConnections.MqttLoginedList.remove(i);
                MqttMessage message = new MqttMessage("success".getBytes());
                mqttClientEntity.publish("M/"+content+"/quitLogin",message);
                return;
            }
        }
        MqttMessage message = new MqttMessage("falie！".getBytes());
        message.setQos(2);
        mqttClientEntity.publish("M/"+content+"/quitLogin",message);
    }

    // 退出兼职
    private static void quitPartTime(String content) throws MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        Gson gson = new Gson();
        QuiterAndId quiterAndId = gson.fromJson(content, QuiterAndId.class);
        // 找到要退出的那一个兼职
        for (int i = 0; i <PartTimeManger.PartTimeList.size() ; i++) {
            // 获取兼职信息
            PartTimeEntity partTimeEntity = PartTimeManger.PartTimeList.get(i);
            // 如果兼职id是我们要退的哪一个就进去if分支
            if(partTimeEntity.getId() == quiterAndId.getId()) {
                for (int j = 0; j < partTimeEntity.getArrayList().size(); j++) {
                    // 获取兼职人员信息
                    JoinInfo joinInfo = partTimeEntity.getArrayList().get(j);
                    // 如果是我们本人就从队列中移除这个信息
                    if(joinInfo.getName().equals(quiterAndId.getQuiter())){
                        partTimeEntity.getArrayList().remove(j);
                        MqttMessage message = new MqttMessage("success".getBytes());
                        message.setQos(2);
                        mqttClientEntity.publish("M/quit/quitPart",message);
                        return;
                    }
                }
            }
        }
        MqttMessage message = new MqttMessage("falie".getBytes());
        message.setQos(2);
        mqttClientEntity.publish("M/quit/quitPart",message);
    }

    // 检查clientId创建的兼职
    private static void check(String content) throws MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        Gson gson = new Gson();
        CreaterAndId createrAndId = gson.fromJson(content, CreaterAndId.class);
        // 遍历当前存在的所有兼职信息
        for (int i = 0; i < PartTimeManger.PartTimeList.size(); i++) {
            PartTimeEntity partTimeEntity = PartTimeManger.PartTimeList.get(i);
            if(partTimeEntity.getCreater().equals(createrAndId.getCreater()) && partTimeEntity.getId() == createrAndId.getId()){
                MqttMessage message = new MqttMessage(String.valueOf(partTimeEntity.getId()).getBytes());
                message.setQos(2);
                mqttClientEntity.publish("M/"+createrAndId.getCreater()+"/check",message);
                return;
            }
        }
        MqttMessage message = new MqttMessage("falie".getBytes());
        message.setQos(2);
        mqttClientEntity.publish("M/"+createrAndId.getCreater()+"/check",message);
    }

    // 判断用户是否登录
    private static void isLogin(String content) throws MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        for (int i = 0; i < MqttConnections.MqttLoginedList.size(); i++) {
            String loginedList = MqttConnections.MqttLoginedList.get(i);
            System.out.println(loginedList);
            if(loginedList.equals(content)){
                MqttMessage message = new MqttMessage("logined".getBytes());
                message.setQos(2);
                mqttClientEntity.publish("U/"+content+"/isLogin",message);
                return;
            }
        }
        MqttMessage message = new MqttMessage("notlogin".getBytes());
        message.setQos(2);
        mqttClientEntity.publish("U/"+content+"/isLogin",message);
    }

    // 加入兼职
    private static void joinPartTime(String content) throws MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        System.out.println(content);
        Gson gson = new Gson();
        JoinInfo joinInfo = gson.fromJson(content, JoinInfo.class);
        // 判断用户要加入哪一个兼职
        for (int i = 0; i < PartTimeManger.PartTimeList.size() ; i++) {
            PartTimeEntity partTimeEntity = PartTimeManger.PartTimeList.get(i);
            if(partTimeEntity.getId() == joinInfo.getPartTimeId()){
                System.out.println("值相等");
                System.out.println(joinInfo.getName());
                partTimeEntity.setArrayList(joinInfo);
                MqttMessage message = new MqttMessage(String.valueOf(partTimeEntity.getId()).getBytes());
                message.setQos(2);
                mqttClientEntity.publish("U/"+joinInfo.getName()+"/EPjoin",message);
                break;
            }
            // 如果还找到就返回错误信息
            if(i == PartTimeManger.PartTimeList.size() -1){
                MqttMessage message = new MqttMessage("falie".getBytes());
                message.setQos(2);
                mqttClientEntity.publish("U/"+joinInfo.getName()+"/EPjoin",message);
            }
        }
    }

    // 查看兼职
    private static void lookPartTime(String content) throws MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        MqttMessage message = new MqttMessage(PartTimeManger.PartTimeList.toString().getBytes());
        message.setQos(2);
        mqttClientEntity.publish("M/"+content+"/Elist",message);
    }

    /**
     * @Description  : 创建兼职流程
     * @author       : flj
     * @param        : content 兼职信息
     * @return       : 空
     * @exception    : MqttException
     * @date         : 2020/9/6 12:07
     */
    private static void createPartTime(String content) throws MqttException{
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        Gson gson = new Gson();
        PartTimeEntity partTimeEntity = gson.fromJson(content, PartTimeEntity.class);
        System.out.println("是否同意创建（1同意，0不同意）：");
        Scanner scanner = new Scanner(System.in);
        int i = scanner.nextInt();
        if(i == 0){
            // 告诉创建者消息
            MqttMessage message = new MqttMessage("管理员不同意".getBytes());
            message.setQos(2);
            mqttClientEntity.publish("E/"+partTimeEntity.getCreater()+"/MPrelease",message);
        }else {
            String partTime = PartTimeManger.createPartTime(partTimeEntity);
            if(partTime !=null) {
                MqttMessage message = new MqttMessage(String.valueOf(PartTimeManger.PartTimeList.get(Integer.parseInt(partTime)-1).getId()).getBytes());
                mqttClientEntity.publish("E/" + partTimeEntity.getCreater() + "/MPrelease", message);
            }
        }
    }

    // 执行登录流程
    private static void login(String content) throws MqttException {
        System.out.println("login");
        Gson gson = new Gson();
        LoginEntity loginEntity = gson.fromJson(content, LoginEntity.class);
        // 静态类
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        // 先检查是否已经登录
        for (int i = 0; i < MqttConnections.MqttLoginedList.size() ; i++) {
            String isLoginedMap = MqttConnections.MqttLoginedList.get(i);
            if(isLoginedMap.equals(loginEntity.getClientId())){
                MqttMessage message = new MqttMessage((loginEntity.getClientId()+"已经登录，请勿重复登录").getBytes());
                mqttClientEntity.publish("U/"+loginEntity.getClientId()+"/MUreg",message);
                return;
            }
        }
        // 如果未登录则查看存不存在
        if(MqttConnections.checkIsLoginInfo(loginEntity)){
            MqttMessage message = new MqttMessage("success".getBytes());
            message.setQos(2);
            mqttClientEntity.publish("U/"+loginEntity.getClientId()+"/MUreg",message);
            // 获取对象并存入到登录集合队列中
            MqttConnections.MqttLoginedList.add(loginEntity.getClientId());
        }else {
            MqttMessage message = new MqttMessage("账号密码错误或用户不存在".getBytes());
            message.setQos(2);
            mqttClientEntity.publish("U/"+loginEntity.getClientId()+"/MUreg", message);
        }
    }

    // 执行注册的流程
    private static void register(String content) throws SQLException, MqttException {
        MqttClient mqttClientEntity = ManServer.getMqttClientEntity();
        System.out.println("是否同意注册（1同意，0不同意）：");
        Scanner scanner = new Scanner(System.in);
        int i = scanner.nextInt();

        if(i == 0){
            MqttMessage message = new MqttMessage("管理员不同意".getBytes());
            message.setQos(2);
            mqttClientEntity.publish("U/192.168.1.10/Ureg",message);
        }else {
            Gson gson = new Gson();
            LoginEntity loginEntity = gson.fromJson(content, LoginEntity.class);
            // 如果账号不存在的情况
            if (MqttConnections.checkIsExist(loginEntity.getUsername(),loginEntity.getClientId()) == null) {
                MqttConnections.register(loginEntity);
                MqttMessage message = new MqttMessage("success".getBytes());
                message.setQos(2);
                mqttClientEntity.publish("U/192.168.1.10/Ureg", message);
            } else {
                // 如果存在的话就提醒失败
                MqttMessage message = new MqttMessage("账号已存在".getBytes());
                mqttClientEntity.publish("U/192.168.1.10/Ureg", message);
            }
        }
    }
}
