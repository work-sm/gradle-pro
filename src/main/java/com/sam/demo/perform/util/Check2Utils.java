package com.sam.demo.perform.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

@Slf4j
public class Check2Utils {

    public static void process(String[] command, String path, String commod) throws Exception {
        Semaphore semaphore = new Semaphore(1);
        Process process = new ProcessBuilder(command)
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
            } catch (Exception e) {
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
            } catch (Exception e) {
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
            bw.write(commod);
//            bw.flush();
            semaphore.acquire();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
//            process(new String[]{"pwd"}, "/home/sam/product/exe", "echo hello");

            Process p = new ProcessBuilder("./tmdp", "0202")
                    .directory(new File("/home/sam/product/exe"))
                    .start();
//            Process p = Runtime.getRuntime().exec("C:/runtime/pod0/POD_windows/test.bat");
            InputStream ins= p.getInputStream();
            InputStream ers= p.getErrorStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins, Charset.forName("GBK")));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(ers, Charset.forName("GBK")));
            String line;
            while ((line = inputReader.readLine()) != null) {
                log.info("[in ]" + line);
            }
            while ((line = errReader.readLine()) != null) {
                log.error("[err ]" + line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.printf("123");
    }

}
