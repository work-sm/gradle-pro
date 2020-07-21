package com.sam.demo.process2;

import java.io.*;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) throws IOException {
        String exe = "ascNodeCompt.exe";
        Process process = new ProcessBuilder("cmd")
                .directory(new File("C:\\Users\\Administrator\\Desktop\\software\\bin"))
//                .redirectErrorStream(true)
                .start();
        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        WriterConsole writerConsole = new WriterConsole(outputStream);
        writerConsole.start();
        new Thread() {
            public void run() {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
                try {
                    String line1 = null;
                    while ((line1 = br1.readLine()) != null) {
                        System.out.println(line1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            public void  run() {
                BufferedReader br2 = new BufferedReader(new  InputStreamReader(errorStream, Charset.forName("GBK")));
                try {
                    String line2 = null ;
                    while ((line2 = br2.readLine()) !=  null ) {
                            System.err.println(line2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    try {
                        errorStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        try {
            writerConsole.exec(exe);

            process.waitFor();
            System.out.println("213");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writerConsole.close();
    }
}
