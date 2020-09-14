package com.sam.demo.snappy;

import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.apache.hadoop.record.meta.TypeID.RIOType.BUFFER;

public class TestSnappy {

    public static void main(String[] args) throws IOException {
        String dataString = "The quick brown fox jumps over the lazy dog";
        byte[] compressedData = Snappy.compress(dataString.getBytes());
        System.out.println("Snappy.compress  压缩结果：" + bytes2hex(compressedData));
        byte[] compressedData2 = compressSnappy(dataString.getBytes());
        System.out.println("SnappyInputStream压缩结果：" + bytes2hex(compressedData2));
    }

    public static byte[] compressSnappy(byte[] data) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SnappyOutputStream sos = new SnappyOutputStream(os);
        int count;
        byte temp[] = new byte[BUFFER];
        try {
            while ((count = is.read(temp)) != -1) {
                sos.write(temp, 0, count);
            }
            sos.flush();
            byte[] output = os.toByteArray();
            return output;
        } finally {
            sos.close();
            is.close();
        }
    }

    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp).append(" ");
        }
        return sb.toString();
    }
}
