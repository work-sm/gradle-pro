package com.sam.demo.perform.util;

import com.alibaba.fastjson.JSONPath;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class JsonUtils {

    public static void main(String[] args) throws IOException {
        String val = getVal("C:\\Users\\Administrator\\Desktop\\software\\data\\inputData\\satPara_normal.json", "$.name");
        System.out.println(val);
    }

    public static String getVal(String filename, String path) throws IOException {
        String string = getString(filename);
        return (String) JSONPath.read(string, path);
    }

    public static String getVal(File file, String path) throws IOException {
        String string = getString(file);
        return (String) JSONPath.read(string, path);
    }

    private static String getString(String filename) throws IOException {
        InputStream inputStream = new FileInputStream(filename);
        return IOUtils.toString(inputStream, Charset.defaultCharset());
    }

    private static String getString(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        return IOUtils.toString(inputStream, Charset.defaultCharset());
    }

}
