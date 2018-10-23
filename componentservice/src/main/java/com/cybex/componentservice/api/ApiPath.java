package com.cybex.componentservice.api;


import com.cybex.componentservice.BuildConfig;

/**
 * 接口请求地址全部配置在这里
 * <p>
 * Created by wanglin on 2018/7/05
 */

public class ApiPath {

    //测试环境
    public static final String HOST_TEST = "";
    //开发环境
    public static final String HOST_DEV = "";
    //线上环境
    public static final String HOST_ONLINE = "https://api.xxx.com";
    public final static String[] HOST = {HOST_TEST, HOST_DEV, HOST_ONLINE};
    public static String REST_URI_HOST = HOST[BuildConfig.API_PATH];


    /**
     * ------------EOS 项目情况配置---------------
     */

    //中心化服务器host
    public static final String HOST_CENTER_SERVER = "http://139.196.73.117:3005";
    //"http://139.196.73.117:3002"微信支付沙盒测试环境
    //"http://139.196.73.117:3001"真实环境
    //"http://139.196.73.117:3005"灰度staging环境

    //链上服务器host
    private static String HOST_ON_CHAIN = "http://api-mainnet.starteos.io";
        //http://47.75.154.248:50003 北京团队测试链
    //http://52.77.177.200:8888 cybex eos 主链节点

    //eosweb.net,一个外网节点，拥有当前eos链上所有数据
    public static final String HOST_EOS_WEB = "https://eosweb.net";

    //价格地址
    public static final String URL_UNIT_PRICE = "https://app.cybex.io/price";

    //区块链浏览器地址
    public static final String URL_BLOCK_CHAIN_BROWSER = "https://eosflare.io/tx/";

    /**------------EOS 项目情况配置---------------*/


    /**------------URL 地址配置---------------*/
    //服务协议
    public static final String TERMS_OF_SERVICE_CN = "https://nebuladownload.oss-cn-beijing.aliyuncs"
            + ".com/gemma/gemma_policy_cn" + ".html";
    public static final String TERMS_OF_SERVICE_EN = "https://nebuladownload.oss-cn-beijing.aliyuncs"
            + ".com/gemma/gemma_policy_en" + ".html";

    //帮助与反馈
    public static final String HELP_CN = "http://47.75.154.39:3009/gemma?lang=cn.html";
    public static final String HELP_EN = "http://47.75.154.39:3009/gemma?lang=en";

    //版本说明
    public static final String VERSION_NOTE_CN = "https://nebuladownload.oss-cn-beijing.aliyuncs"
            + ".com/gemma/gemma_release_desc_cn"
            + ".html";
    public static final String VERSION_NOTE_EN = "https://nebuladownload.oss-cn-beijing.aliyuncs"
            + ".com/gemma/gemma_release_desc_en"
            + ".html";

    //signupeoseos Dapp 地址
    public static final String DAPP_SINGUP_EOS = "https://mp.weixin.qq.com/s/wvrlzbj3EGv78s3gjoCvjw";


    /**------------URL 地址配置---------------*/



    /**------------节点 地址配置---------------*/

    public static final String EOS_CYBEX = "http://52.77.177.200:8888";
    public static final String EOS_START_EOS = "http://api-mainnet.starteos.io";
    public static final String EOS_NEW_YORK = "https://api.eosnewyork.io";
    public static final String EOS_GREY_MASS = "https://eos.greymass.com";
    public static final String EOS_AISA = "https://api-direct.eosasia.one";
    public static final String EOS_GRAVITY = "https://api-mainnet.eosgravity.com";
    public static final String EOS_HELLOEOS = "https://api.helloeos.com.cn";
    public static final String EOS_HK_EOS = "https://api.hkeos.com";
    public static final String EOS_42 = "https://nodes.eos42.io";
    public static final String EOS_CYPHER_GLASS = "https://api.cypherglass.com";
    public static final String EOS_TEST_BEIJING = "http://47.75.154.248:50003";

    /**------------节点 地址配置---------------*/

    public static String getHOST_ON_CHAIN() {
        return HOST_ON_CHAIN;
    }

    public static void setHOST_ON_CHAIN(String newUrl) {
        HOST_ON_CHAIN = newUrl;
    }

    /**------------节点 地址配置---------------*/
}
