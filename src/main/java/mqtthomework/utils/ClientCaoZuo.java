package mqtthomework.utils;

public enum ClientCaoZuo {

    // 登录成功
    USER_REGISTER_SUCCESS("MUreg",1),
    // 创建兼职成功
    USER_CREATEPARTTIMESUCCESS("MPrelease",2),
    // 加入兼职
    USER_JOINPARTTIMESUCCESS("EPjoin",3),
    // 是否登录
    USER_ISNOTLOGIN("isLogin",4),
    // 注册
    USER_REGISTERINFO("Ureg",5),
    // 检查是否创建兼职
    USER_CHECKISNOTCREATEPARTTIME("check",6),
    // 退出兼职
    USER_QUITPARTTIME("quitPart",7),
    // 登出
    USER_LOGINOUT("quitLogin",8),
    // 查看兼职列表
    USER_PARTTIMELIST("Elist",9),
    // 查看兼职列表
    USER_LISTEN("listen",10);

    private String desc;//文字描述
    private Integer code; //对应的代码

    /**
     * 私有构造,防止被外部调用
     * @param desc
     */
    private ClientCaoZuo(String desc, Integer code){
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
        for (ClientCaoZuo a: ClientCaoZuo.values()) {
            if(doing.equals(a.getDesc())){
                return a.getCode();
            }
        }
        return -1;
    }



}
