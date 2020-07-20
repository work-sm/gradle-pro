package com.sam.demo.process.down;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Downloader {

    private static HttpURLConnection getConn(String url) throws IOException {
        URL get = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) get.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "close");
        return conn;
    }

    public static List<String> getDetails(InputStream is) throws IOException {
        List<String> details = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 1024 * 1024);
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                details.add(strRead.trim());
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return details;
    }

    public static List<String> download(String url) throws IOException {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            log.info("downloading... {}", url);
            conn = Downloader.getConn(url);
            conn.connect();
            int resCode = conn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                log.info("pro data!!!");
                long l1 = System.currentTimeMillis();
                List<String> details = Downloader.getDetails(is);
                log.info("pro data {} ms", System.currentTimeMillis() - l1);
                return details;
            } else {
                String msg = URLDecoder.decode(conn.getResponseMessage(), "UTF-8");
                throw new IOException("Failed to get : code" + resCode + " message:" + msg);
            }
        } finally {
            IOUtils.closeQuietly(is);
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
