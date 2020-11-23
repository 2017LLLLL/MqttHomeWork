package mqtthomework.server;

import mqtthomework.callback.MessageCallBack;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


// 服务端，先启动这个
public class ManServer {

    private static MqttClient mqttClient;

    public static MqttClient getMqttClientEntity(){
        return mqttClient;
    }

    // 创建一个MQTT连接
    public void getMqttClient() throws MqttException {
        String broker = "tcp://127.0.0.1:1883";
        String clientId = "server1";
        MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());  // 创建客户端
        MqttConnectOptions connOpts = new MqttConnectOptions(); // 创建链接参数
        connOpts.setCleanSession(false); // 在重新启动和重新连接时记住状态
        sampleClient.connect(connOpts); // 建立连接
        mqttClient = sampleClient;
    }


    public static void main(String[] args) throws MqttException {
        ManServer manServer = new ManServer();
        manServer.getMqttClient();
        // 设置回调函数
        mqttClient.setCallback(new MessageCallBack());
        // 先订阅所有消息
        mqttClient.subscribe("#",2);
    }

}
