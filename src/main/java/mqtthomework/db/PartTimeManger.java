package mqtthomework.db;

import mqtthomework.entity.PartTimeEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @PackageName mqtthomework.db
 * @Classname PartTimeManger
 * @Description 兼职管理DB
 * @Date 2020/9/6 11:34
 * @Author flj
 * @Version 1.0.0
 */
public class PartTimeManger {

    // 存放兼职信息的队列
    public static ArrayList<PartTimeEntity> PartTimeList = new ArrayList<PartTimeEntity>();

    // 新建兼职
    public static String createPartTime(PartTimeEntity partTimeEntity){
        partTimeEntity.setId(PartTimeList.size()+1);
        PartTimeList.add(partTimeEntity);
        return String.valueOf(partTimeEntity.getId());
    }

}
