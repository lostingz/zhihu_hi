/**
 * Chsi
 * Created on 2016年3月1日
 */
package com.fun.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.config.HeaderConfig;
import com.fun.config.UserInfoConfig;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class zhihuTool {
    public static Header[] cookies = null;

    private static List<NameValuePair> generateLoginData(String username, String password) throws Exception {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("username", username));
        data.add(new BasicNameValuePair("password", password));
        data.add(new BasicNameValuePair("grant_type", "password"));
        data.add(new BasicNameValuePair("client_id", "8d5227e0aaaa4797a763ac64e0c3b8"));
        data.add(new BasicNameValuePair("source", "com.zhihu.android"));
        long timestamp = System.currentTimeMillis();
        data.add(new BasicNameValuePair("timestamp", String.valueOf(timestamp)));
        data.add(new BasicNameValuePair("signature", HeaderConfig.getSignature(timestamp)));
        return data;
    }

    private static String postLogin(HttpClient httpClient) throws Exception {
        String url = "https://api.zhihu.com/sign_in";
        List<NameValuePair> postData = generateLoginData(UserInfoConfig.EMAIL, UserInfoConfig.PASSWD);
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "oauth 8d5227e0aaaa4797a763ac64e0c3b8");
        post.setHeader("Cookie", cookies[0].getValue().split(";")[0]);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, "UTF-8");
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        String responseJsonStr = EntityUtils.toString(response.getEntity());
        JSONObject json = JSON.parseObject(responseJsonStr);
        return json.getString("access_token");
    }

    private static boolean isShowCaptcha(HttpClient httpClient) throws ClientProtocolException, IOException {
        String captchaUrl = "https://api.zhihu.com/captcha";
        HttpGet get = new HttpGet(captchaUrl);
        get.setHeader("Authorization", "oauth 8d5227e0aaaa4797a763ac64e0c3b8");
        HttpResponse response = httpClient.execute(get);
        String responseJsonStr = EntityUtils.toString(response.getEntity());
        JSONObject json = JSON.parseObject(responseJsonStr);
        cookies = response.getHeaders("Set-Cookie");
        return (Boolean) json.get("show_captcha");
    }

    private static void viewCaptcha(HttpClient httpClient) throws ClientProtocolException, IOException {
        String captchaUrl = "https://api.zhihu.com/captcha";
        HttpPut put = new HttpPut(captchaUrl);
        put.setHeader("Authorization", "oauth 8d5227e0aaaa4797a763ac64e0c3b8");
        put.setHeader("Cookie", cookies[0].getValue().split(";")[0]);
        HttpResponse response = httpClient.execute(put);
        String responseJsonStr = EntityUtils.toString(response.getEntity());
        JSONObject json = JSON.parseObject(responseJsonStr);
        byte[] imgbyte = Base64.decodeBase64(json.getString("img_base64"));
        File dir = new File(UserInfoConfig.CAPTCHASAVEPATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir.getAbsolutePath() + File.separator + "captcha.gif");
        file.createNewFile();
        OutputStream out = new FileOutputStream(file);
        out.write(imgbyte);
        out.close();
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("mspaint.exe " + dir.getAbsolutePath() + File.separator + "captcha.gif");
    }

    private static void inputCaptcha() throws ClientProtocolException, IOException {
        HttpClient httpClient = zhihuTool.getHttpsClient();
        String captchaUrl = "https://api.zhihu.com/captcha";
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        System.out.println("input captcha:");
        Scanner scanner = new Scanner(System.in);
        String captcha = scanner.next();
        postData.add(new BasicNameValuePair("input_text", captcha));
        HttpPost post = new HttpPost(captchaUrl);
        post.setHeader("Authorization", "oauth 8d5227e0aaaa4797a763ac64e0c3b8");
        post.setHeader("Cookie", cookies[0].getValue().split(";")[0]);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, "UTF-8");
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        String responseJsonStr = EntityUtils.toString(response.getEntity());
        System.out.println(responseJsonStr);
    }

    public static String login(HttpClient httpClient) throws Exception {
        boolean isInputCaptcha = zhihuTool.isShowCaptcha(httpClient);
        if (isInputCaptcha) {
            zhihuTool.viewCaptcha(httpClient);
            zhihuTool.inputCaptcha();
        }
        return zhihuTool.postLogin(httpClient);
    }

    public static HttpClient getHttpsClient() {
        HttpClient httpClient = null;
        SSLContext context;
        try {
            context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

            }}, new SecureRandom());

            HostnameVerifier verifier = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(context, verifier);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpClient;
    }

    public static void writeTokenToFile(String token) throws IOException {
        File f = new File("access_token.txt");
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(token.getBytes());
        fos.close();
    }

    public static String getToken() throws IOException {
        File f = new File("access_token.txt");
        if (!f.exists()) {
            f.createNewFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(f));
        return br.readLine();
    }

}
