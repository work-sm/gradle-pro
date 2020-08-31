package com.sam.demo.perform.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

@Slf4j
public class CheckUtils {

    public static final String path = "C:/runtime/tle0/bin";

    public static String process(String tle) throws Exception {
        Semaphore semaphore = new Semaphore(1);
        Process process = new ProcessBuilder("cmd")
                .directory(new File(path))
                .start();

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, Charset.forName("GBK")));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));

        new Thread(() -> {
            try {
                String line;
                boolean isOk = false;
                while ((line = inputReader.readLine()) != null) {
                    log.info("[in ]" + line);
                    if (line.contains("TEST_IS_OK")) {
                        isOk = true;
                    }
                }
                if(isOk){
                    semaphore.release();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            try {
                String line;
                boolean isErr = false;
                while ((line = errorReader.readLine()) != null) {
                    log.error("[err]" + line);
                    isErr = true;
                }
                if (isErr) {
                    semaphore.release();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    errorStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            FileUtils.write(new File(path, "TLE.txt"), tle, "UTF-8");

            bw.write("@echo off\nTLE_J2000KEPL\necho TEST_IS_OK\n");
            bw.flush();
            semaphore.acquire();
            Thread.sleep(1000);

            return FileUtils.readFileToString(new File(path,"J2000KEPL.TXT"), "UTF-8");
        } finally {
            try {
                outputStream.close();
//                FileUtils.forceDelete(new File(path,"J2000KEPL.TXT"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            String test = process("\t\n" +
                    "1 44316U 19032G   20238.85907804 -.00000048  00000-0  14618-4 0  9996\n" +
                    "2 44316  44.9876 121.2692 0017758 271.3055  88.5771 14.99031526 67157");
            System.out.println(test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
