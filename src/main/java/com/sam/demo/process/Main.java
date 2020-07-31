package com.sam.demo.process;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Main {

    private static Scanner scan = new Scanner(System.in);

    //Desktop.getDesktop().open
    public static void main(String[] args) throws IOException {
        Process process = new ProcessBuilder("cmd")
                .directory(new File("C:\\Users\\Administrator\\Desktop\\software\\bin"))
//                .redirectErrorStream(true)
                .start();
        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        new Thread() {
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
                try {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
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
                BufferedReader br = new BufferedReader(new  InputStreamReader(errorStream, Charset.forName("GBK")));
                try {
                    String line = null ;
                    while ((line = br.readLine()) !=  null ) {
                            System.err.println(line);
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

        new Thread() {
            public void  run() {
                BufferedWriter bw = new BufferedWriter(new  OutputStreamWriter(outputStream));
                try {
                    while (true) {
                        String line = getIn();
                        bw.write(line);
                        bw.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private static String getIn(){
        if (scan.hasNext()) {
            return scan.next();
        }
        return "";
    }

}
