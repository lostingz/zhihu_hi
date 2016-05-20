/**
 * Chsi
 * Created on 2016年3月4日
 */
package com.fun.test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.fun.tool.zhihuTool;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class HttpsTest {
    public static void main(String[] args) {
        try {
            HttpClient httpClient = zhihuTool.getHttpsClient();
            String url = "https://pic2.zhimg.com/6f7c6805962dc58cc21d4df748f18125_xl.jpg";
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            byte[] b = new byte[1024];
            int len = 0;
            OutputStream out = new FileOutputStream("E:/testImage/" + "httpstest.jpg");
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
