package mqtthomework.entity;

/**
 * @PackageName mqtthomework.entity
 * @Classname CreaterAndId
 * @Description 兼职创建者和对应id
 * @Date 2020/9/6 23:25
 * @Author flj
 * @Version 1.0.0
 */
public class CreaterAndId {

    private String creater;
    private int id;

    public CreaterAndId(String creater, int id) {
        this.creater = creater;
        this.id = id;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
