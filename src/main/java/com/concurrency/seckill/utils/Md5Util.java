package com.concurrency.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Md5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassToFormPass(String inputPass) {
        return md5(inputPass + salt);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        return md5(formPass + salt);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        return formPassToDBPass(inputPassToFormPass(inputPass), salt);
    }

    public static void main(String[] args) {
        System.out.println(Md5Util.formPassToDBPass(Md5Util.inputPassToFormPass("123456"), salt));
        System.out.println(Md5Util.inputPassToDBPass("123456", salt));
    }
}
