package com.sam.demo.perform.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;

@Slf4j
public class Check2Utils {

    public static void process(String path, String commod) throws Exception {
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
            bw.write("@echo off\n"+commod+"\necho TEST_IS_OK\n");
            bw.flush();
            semaphore.acquire();
            Thread.sleep(1000);
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
            process("C:/runtime/pod0/POD_windows", "PODMenu -path C:/runtime/pod0/POD/ODTKS -sat TT02 -mdp -pod -op");
//            process("C:/runtime/pod0/POD_windows", "test.bat");

            Process p = new ProcessBuilder("C:\\runtime\\pod0\\POD_windows\\PODMenu.exe","-path C:/runtime/pod0/POD/ODTKS", "-sat TT02", "-mdp", "-pod", "-op")
                    .directory(new File("C:\\runtime\\pod0\\POD_windows"))
                    .start();
//            Process p = Runtime.getRuntime().exec("C:/runtime/pod0/POD_windows/PODMenu.exe -path C:/runtime/pod0/POD/ODTKS -sat TT02 -mdp -pod -op");
//            Process p = Runtime.getRuntime().exec("C:/runtime/pod0/POD_windows/test.bat");
            InputStream ins= p.getInputStream();
            InputStream ers= p.getErrorStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins, Charset.forName("GBK")));
            String line;
            while ((line = inputReader.readLine()) != null) {
                log.info("[in ]" + line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
