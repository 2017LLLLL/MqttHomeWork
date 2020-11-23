package mqtthomework.entity;


import java.util.ArrayList;
import java.util.Date;

/**
 * @PackageName mqtthomework.entity
 * @Classname PartTimeEntity
 * @Description 兼职实体类
 * @Date 2020/9/4 22:14
 * @Author flj
 * @Version 1.0.0
 */
public class PartTimeEntity {
    private int id;
    private Date time;
    private int personNum;
    private String place;
    private String creater;
    private ArrayList<JoinInfo> arrayList = new ArrayList<JoinInfo>();

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "PartTimeEntity{" +
                "id=" + id +
                ", time=" + time +
                ", personNum=" + personNum +
                ", place='" + place + '\'' +
                ", creater='" + creater + '\'' +
                ", arrayList=" + arrayList +
                '}';
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public void setArrayList(ArrayList<JoinInfo> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<JoinInfo> getArrayList() {
        return arrayList;
    }

    public void setArrayList(JoinInfo joinInfo) {
        this.arrayList.add(joinInfo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PartTimeEntity(Date time, int personNum, String place) {
        this.time = time;
        this.personNum = personNum;
        this.place = place;
    }

    public PartTimeEntity(int id,Date time, int personNum, String place) {
        this.id = id;
        this.time = time;
        this.personNum = personNum;
        this.place = place;
    }

    public PartTimeEntity(Date time, int personNum, String place, String creater) {
        this.time = time;
        this.personNum = personNum;
        this.place = place;
        this.creater = creater;
    }
}
