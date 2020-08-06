package com.sam.demo.exe.impl;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.pool.WorkPool;
import com.sam.demo.exe.resources.impl.SleepWaitProcedure;
import com.sam.demo.exe.resources.Executor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        WorkPool<Carrier> carrierWorkPool = new WorkPool<>();
        String[] params = new String[]{
                "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                        "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436",
                "1 00902U 64063E   20202.50348760  .00000030  00000-0  31316-4 0  9990\n" +
                        "2 00902  90.1619  31.5795 0018818  17.0373  41.7246 13.52685257565000",
                "1 00900U 64063C   20202.17095681  .00000187  00000-0  19107-3 0  9998\n" +
                        "2 00900  90.1522  28.9147 0025875 322.1024 146.6777 13.73394820774941"
        };

        Writer writer = new Writer("C:\\runtime\\tle0\\bin\\TLE.txt");
        Reader reader = new Reader("C:\\runtime\\tle0\\bin\\J2000KEPL.TXT");
        Executor<Carrier> executor = new SleepWaitProcedure<>("C:\\runtime\\tle0\\bin", "TLE_J2000KEPL.exe", 1000);
        carrierWorkPool.register(writer);
        carrierWorkPool.register(reader);
        carrierWorkPool.register(executor);
        carrierWorkPool.start();

        //运行
        for (String param : params) {
            Carrier carrier = new Carrier();
            carrier.setUnique(UUID.randomUUID().toString());
            carrier.setWriterContent(param);
            carrierWorkPool.addWork(carrier);
        }
    }

}
