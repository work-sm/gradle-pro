package com.sam.demo.nerver.qos.type;

/**
 * 流控级别
 * <p>
 * 渠道/服务/场景/会话/地址/主机
 * <p>
 * 放通率:100%
 * <p>
 * @author lry
 */
public enum QosLevel {

	/**
	 * 渠道ID
	 */
    CHANNEL("CHANNEL","渠道ID"),

    /**
     * 服务ID
     */
    SERVICE("SERVICE","服务ID"),

    /**
     * 场景ID
     */
    SCENARIO("SCENARIO","场景ID"),

    /**
     * 会话ID
     */
    SESSION("SESSION","会话ID"),

    /**
     * 地址
     */
    ADDRESS("ADDRESS","地址"),

    /**
     * 主机
     */
    HOST("HOST","主机");

    private String code;
    private String msg;

    QosLevel(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public static QosLevel convert(String code) throws Exception {
        return valueOf(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}