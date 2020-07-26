package com.sam.demo.process1.exec;

import com.sam.demo.process1.AbstractProcessor;
import com.sam.demo.process1.Processor;
import com.sam.demo.process1.work.Element;
import com.sam.demo.process1.work.SimpleProductLine;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TleProcessor extends AbstractProcessor {

    private RandomAccessFile raf;
    private RandomAccessFile bfr;
    private File tle;
    private File kepl;

    public TleProcessor(String exeFullName) throws IOException {
        super(exeFullName);
        tle = new File(home(), "TLE.txt");
        kepl = new File(home(), "J2000KEPL.TXT");
        if (!tle.exists() || tle.isDirectory()) {
            tle.delete();
            tle.createNewFile();
        }
        if (!kepl.exists() || kepl.isDirectory()) {
            kepl.delete();
            kepl.createNewFile();
        }
        // 重要参数,完成信号
        sign("Stop - Program terminated.");
        raf = new RandomAccessFile(tle, "rw");
        bfr = new RandomAccessFile(kepl, "r");
    }

    @Override
    protected long waitTime() {
        return 0;
    }

    @Override
    protected long timeout() {
        return 0;
    }

    @Override
    protected void next(Element element) throws Exception {
        String params = element.getParams();
        raf.setLength(0);
        raf.seek(0);
        raf.write(params.getBytes());
    }

    /**
     * 只是缓存 mark reset
     * 文件指针 seek
     */
    @Override
    protected void completed(Element element) throws Exception {
        bfr.seek(0);
        String s = bfr.readLine();
        element.setResult(s);
        element.setResultFiles(new File[]{kepl});
        element.setParamsFile(tle);
    }

    @Override
    protected void error(Element element) {
    }

    public void close() throws IOException, InterruptedException {
        super.close();
        if (raf != null) {
            raf.close();
        }
        if (bfr != null) {
            bfr.close();
        }
    }

    public static void main(String[] args) throws IOException{
        String[] params = new String[]{
                "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                        "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436",
                "1 00902U 64063E   20202.50348760  .00000030  00000-0  31316-4 0  9990\n" +
                        "2 00902  90.1619  31.5795 0018818  17.0373  41.7246 13.52685257565000",
                "1 00900U 64063C   20202.17095681  .00000187  00000-0  19107-3 0  9998\n" +
                        "2 00900  90.1522  28.9147 0025875 322.1024 146.6777 13.73394820774941"
        };

        SimpleProductLine simpleProductLine = new SimpleProductLine();
        ExecutorService service = Executors.newFixedThreadPool(3);
        Processor processor1 = new TleProcessor("C:\\Users\\Administrator\\Desktop\\runtime\\tle1\\TLE_J2000KEPL.exe");
        Processor processor2 = new TleProcessor("C:\\Users\\Administrator\\Desktop\\runtime\\tle2\\TLE_J2000KEPL.exe");
        Processor processor3 = new TleProcessor("C:\\Users\\Administrator\\Desktop\\runtime\\tle3\\TLE_J2000KEPL.exe");
        processor1.consume(simpleProductLine);
        processor2.consume(simpleProductLine);
        processor3.consume(simpleProductLine);
        service.execute(processor1);
        service.execute(processor2);
        service.execute(processor3);
        service.shutdown();

        Element element;
        for (String param : params) {
            element = new Element();
            element.setParams(param);
            processor1.produce(element);
            processor2.produce(element);
            processor3.produce(element);
        }
    }

}
