package com.springrain.erp.modules.dingding;


/**
 * 企业应用接入时的常量定义
 */
public class Env {

    /**
     * 企业应用接入秘钥相关
     */
    public static final String CORP_ID = "ding2c02d1163b34233a35c2f4657eb6378f";
    public static final String CORP_SECRET = "uWQ_nv80YoOEBwvMKI9YIMPnnl5der3V7UWYmN0mc6wcQnm_dbUqA8hPO9bUfT9A";
    public static final String SSO_Secret = "qpjbPTRO0ndftyXii64Mv5EP3MsQF0JlGolqvST9inXs3fXY7E6Na-8lrtNTTz2o";

    /**
     * DING API地址
     */
	public static final String OAPI_HOST = "https://oapi.dingtalk.com";
    /**
     * 企业应用后台地址，用户管理后台免登使用
     */
	public static final String OA_BACKGROUND_URL = "";


    /**
     * 企业通讯回调加密Token，注册事件回调接口时需要传递给钉钉服务器
     */
	public static final String TOKEN = "123456";
	public static final String ENCODING_AES_KEY = "21cq6q1gsl59sh5oyjxauygcitsboxxccc02xr2zgpb";
	
}
