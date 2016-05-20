/**
 * Chsi
 * Created on 2016年5月12日
 */
package com.fun.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import com.fun.config.UserInfoConfig;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class CaptchaTest {
    public static void main(String[] args) throws Exception {
        FileReader b = new FileReader(new File("e:/base64.txt"));
        BufferedReader br = new BufferedReader(b);
        String str = br.readLine();
        String test = "";
        while (str != null) {
            test += str;
            str = br.readLine();
        }

        byte[] imgbyte = Base64.decodeBase64(test);
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
}
