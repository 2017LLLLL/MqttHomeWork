package mqtthomework.entity;


/**
 * @PackageName mqtthomework.entity
 * @Classname QuiterAndId
 * @Description 退出信息实体类
 * @Date 2020/9/4 22:14
 * @Author flj
 * @Version 1.0.0
 */
public class QuiterAndId {
    private String quiter;
    private int id;

    public String getQuiter() {
        return quiter;
    }

    public void setQuiter(String quiter) {
        this.quiter = quiter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuiterAndId(String quiter, int id) {
        this.quiter = quiter;
        this.id = id;
    }
}
