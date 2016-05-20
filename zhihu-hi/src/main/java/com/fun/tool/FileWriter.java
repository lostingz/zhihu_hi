/**
 * Chsi
 * Created on 2016年1月8日
 */
package com.fun.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.fun.model.User;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class FileWriter {
    public static void write(String str) {
        File f = new File("e:\\zhihuSpider.html");
        try {
            newFile(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "utf-8"));
            bw.write(str);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToDisk(User user, HttpClient httpClient) {
        try {
            String fileName = user.getName() + ".jpg";
            HttpGet request = new HttpGet(user.getImgUrl());
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream in = response.getEntity().getContent();
                byte[] b = new byte[1024];
                int len = 0;
                File f = new File("E:/testImage/zhihu/" + fileName);
                if (!f.exists()) {
                    f.createNewFile();
                }
                OutputStream out = new FileOutputStream(f);
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                System.out.println(user.getName() + "get image success");
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("conn error");
        }
    }

    public static void writeAvatar(User user) {
        File f = new File("e:\\zhihuAvatarSpider.html");
        try {
            newFile(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "utf-8"));
            String str = "<div class='userInfo'><p>" + user.getName() + "</p><img alt='' src='" + user.getImgUrl()
                    + "'></div>";
            bw.write(str);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void newFile(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
                BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(f, true));
                String code = "<!DOCTYPE html><html><head><title>"
                        + "</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
                        + "<style>img{width:60px;height:60px;}.userInfo{margin:0px auto;text-align: cneter;float:left}</style>"
                        + "</head><body>";
                bw.write(code);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
