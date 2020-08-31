package com.sam.demo.perform.util;

public class PadUtil {
    /**
     * String左对齐
     * src ：字符串
     * len:满足长度
     * ch:补充字符
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * String右对齐
     * src ：字符串
     * len:满足长度
     * ch:补充字符
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    public static void main(String[] args) {
        String str = "123";
        String s1 = padLeft(str, 5, '0');
        System.out.println(s1);
        String s2 = padRight(str, 5, '0');
        System.out.println(s2);
    }
}
