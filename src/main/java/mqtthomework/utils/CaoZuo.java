package mqtthomework.utils;

public enum CaoZuo {

    // 注册
    USER_REGISTER("Ureg",1),
    // 登录
    USER_LOGIN("Ulog",2),
    // 创建兼职
    USER_CREATE("Prelease",3),
    // 查看兼职
    USER_LOOK("list",4),
    // 加入兼职
    USER_JOIN("join",5),
    // 用户是否登录
    USER_ISLOGIN("isLogin",6),
    // 检查clientId创建对应的兼职
    USER_CHECK("checkPartTime",7),
    // 退出兼职
    USER_QUIT("quitPart",8),
    // 本人退出
    USER_QUITLOGIN("quitUser",9);

    private String desc;//文字描述
    private Integer code; //对应的代码

    /**
     * 私有构造,防止被外部调用
     * @param desc
     */
    private CaoZuo(String desc, Integer code){
        this.desc=desc;
        this.code=code;
    }
    /**
     * 定义方法,返回描述,跟常规类的定义没区别
     * @return
     */
    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 根据传入的操作返回对应的code值
     * @return
     */
    public static int getCode(String doing){
        for (CaoZuo a: CaoZuo.values()) {
            if(doing.equals(a.getDesc())){
                return a.getCode();
            }
        }
        return -1;
    }



}
