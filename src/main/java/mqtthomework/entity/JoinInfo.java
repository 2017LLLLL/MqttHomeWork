package mqtthomework.entity;

/**
 * @PackageName mqtthomework.db
 * @Classname JoinInfo
 * @Description 人员信息（加入兼职人员）实体类
 * @Date 2020/9/6 19:25
 * @Author flj
 * @Version 1.0.0
 */
public class JoinInfo {
    private int partTimeId;
    private String name;
    private String phone;
    private String remark;

    public int getPartTimeId() {
        return partTimeId;
    }

    public void setPartTimeId(int partTimeId) {
        this.partTimeId = partTimeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public JoinInfo(int partTimeId, String name, String phone, String remark) {
        this.partTimeId = partTimeId;
        this.name = name;
        this.phone = phone;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "JoinInfo{" +
                "partTimeId=" + partTimeId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

