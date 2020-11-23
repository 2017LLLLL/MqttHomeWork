package mqtthomework.client;


import mqtthomework.callback.ClientCallBack;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.Scanner;

public class UClientOne {

    // 新启一个客户端
    public static void main(String[] args) throws Exception {
        System.out.println("请输入账号昵称（ClientId）：");
        Scanner scanner = new Scanner(System.in);
        String clientId = scanner.next();
        UClient uClient = new UClient(clientId);
        System.out.println(uClient.hashCode());
        MqttClient mqttClientEntity = uClient.getMqttClient(clientId);
        mqttClientEntity.setCallback(new ClientCallBack(mqttClientEntity,uClient.getUClientName()));
        UClient.showMenu(mqttClientEntity,uClient.getUClientName());
        mqttClientEntity.disconnect(); // 断开连接
        mqttClientEntity.close(); // 关闭客户端
    }
}
