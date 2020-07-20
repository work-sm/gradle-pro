package com.sam.demo.process1;

import com.sam.demo.process1.exec.TleProcessor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String url = "http://www.celestrak.com/NORAD/elements/active.txt";

    private static String[] params = new String[]{
            "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                    "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436",
            "1 00902U 64063E   20202.50348760  .00000030  00000-0  31316-4 0  9990\n" +
                    "2 00902  90.1619  31.5795 0018818  17.0373  41.7246 13.52685257565000",
            "1 00900U 64063C   20202.17095681  .00000187  00000-0  19107-3 0  9998\n" +
                    "2 00900  90.1522  28.9147 0025875 322.1024 146.6777 13.73394820774941"
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(1);
        Processor processor = new TleProcessor("C:\\Users\\Sam\\Desktop\\test1\\TLE_J2000KEPL.exe");

        service.execute(processor);
        service.shutdown();

        for (String param : params) {
            Thread.sleep(500);
            processor.queueUp(param);
        }
    }

}
