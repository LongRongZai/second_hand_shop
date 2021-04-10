package com.shop.utils;

import java.util.Random;

public class VerificationCode {
    public static String generateCode(int n) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            int ran1 = r.nextInt(10);
            sb.append(String.valueOf(ran1));
        }
        return sb.toString();
    }
    public static String verify(String s){

        return null;
    }
}
