package com.sam.demo.process1.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProcessUtils {

    public static void main(String[] args) throws IOException {
        String s = "cmd.exe";
        List<String> pid = getPid(s);
        System.out.println(pid);
        taskillByName(s);
    }

    public static void waitFor(String name, long millis) throws IOException, InterruptedException {
        List<String> pid = getPid(name);
        while (!pid.isEmpty()) {
            Thread.sleep(millis);
            pid = getPid(name);
        }
    }

    public static List<String> getPid(String s) throws IOException {
        StringBuilder buf = new StringBuilder();
        List<String> ps = new ArrayList<>();
        Process process = Runtime.getRuntime().exec("tasklist /fi \"Imagename eq " + s + "\"");
        try {
            Scanner in = new Scanner(process.getInputStream(), "GBK");
            while (in.hasNextLine()) {
                String p = in.nextLine();
                if (p.contains(s)) {
                    for (int i = 0; i < p.length(); i++) {
                        char ch = p.charAt(i);
                        if (ch != ' ') {
                            buf.append(ch);
                        }
                    }
                    ps.add(buf.toString().split("Console")[0].substring(s.length()));
                }
            }
        } finally {
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
            process.destroy();
        }
        return ps;
    }

    public static void taskillByPID(String pid) throws IOException {
        Process process = Runtime.getRuntime().exec("taskkill /pid " + pid + " -t -f ");
        InputStream inputStream = process.getInputStream();
        consumeIO(inputStream);
    }

    public static void taskillByName(String name) throws IOException {
        Process process = Runtime.getRuntime().exec("taskkill /f /im " + name);
        InputStream inputStream = process.getInputStream();
        consumeIO(inputStream);
    }

    /**
     * 流被读取才会执行
     */
    private static void consumeIO(InputStream is) throws IOException {
        try {
            Scanner in = new Scanner(is, "GBK");
            while (in.hasNextLine()) {
                in.nextLine();
            }
        } finally {
            is.close();
        }
    }

}
