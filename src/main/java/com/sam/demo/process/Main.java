package com.sam.demo.process;

import com.sam.demo.process.exec.TleProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String url = "http://www.celestrak.com/NORAD/elements/active.txt";

    private static String[] params = new String[]{
            "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                    "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436"
    };

    public static void main(String[] args) throws Throwable {
        ExecutorService service = Executors.newFixedThreadPool(3);
        Processor processor1 = new TleProcessor("E:/runtime/test1");
        Processor processor2 = new TleProcessor("E:/runtime/test2");
        Processor processor3 = new TleProcessor("E:/runtime/test3");

        service.execute(processor1);
        service.execute(processor2);
        service.execute(processor3);
        service.shutdown();

        List<Processor> processors = new ArrayList<>();
        processors.add(processor1);
        processors.add(processor2);
        processors.add(processor3);

        for (String param : params) {
            Thread.sleep(500);
            processor1.add(param);
        }

//        List<String> download = Downloader.download(url);
//
//        String line1 = "";
//        String line2;
//        for (int i = 0; i < download.size(); i++) {
//            if (i % 3 == 0) {
//            } else if (i % 3 == 1) {
//                line1 = download.get(i);
//            } else {
//                line2 = download.get(i);
//                processors.get(i % 3).add(line1 + "\n" + line2);
//            }
//        }

//        for (Processor processor : processors) {
//            processor.close();
//        }
    }

}
