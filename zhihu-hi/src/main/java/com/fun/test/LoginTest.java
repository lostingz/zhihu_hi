/**
 * Chsi
 * Created on 2016年5月11日
 */
package com.fun.test;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.config.URLConfig;
import com.fun.tool.zhihuTool;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class LoginTest {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = zhihuTool.getHttpsClient();
        String access_key = zhihuTool.getToken();
        if (StringUtil.isBlank(access_key)) {
            access_key = zhihuTool.login(httpClient);
            zhihuTool.writeTokenToFile(access_key);
        }
        String url = URLConfig.SELF_DETAIL_URL + "?access_key=" + access_key;
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", "Bearer " + access_key);
        HttpResponse response = httpClient.execute(get);
        String responseJsonStr = EntityUtils.toString(response.getEntity());
        JSONObject json = JSON.parseObject(responseJsonStr);
        System.out.println(json);
    }
}
