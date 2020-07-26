package com.sam.demo.process1.execute;

import com.sam.demo.process1.Processor;
import com.sam.demo.process1.exec.TleProcessor;
import com.sam.demo.process1.work.Element;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class SimpleExecutor implements Executor {

    private static final Map<Integer, ProcessorCarrier> group = new HashMap<>();
    private static final Random random = new Random();
    private static int limit;

    public SimpleExecutor(int limit) {
        SimpleExecutor.limit = limit;
    }

    @Override
    public void init(Integer id, Processor[] processors){
        group.put(id, new ProcessorCarrier(processors));
    }

    @Override
    public void execute(Integer id, Element element) throws Exception {
        ProcessorCarrier processorCarrier = group.get(id);
        processorCarrier.exec(element);
    }

    static class ProcessorCarrier {
        private final Processor[] processors;

        ProcessorCarrier(Processor[] processors) {
            if (processors.length > limit) throw new IndexOutOfBoundsException("limit " + limit);
            this.processors = processors;
        }

        void exec(Element element) throws Exception {
            int i = random.nextInt(limit);
            processors[i].produce(element);
        }
    }

    public static void main(String[] args) throws IOException {
        String[] params = new String[]{
                "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                        "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436",
                "1 00902U 64063E   20202.50348760  .00000030  00000-0  31316-4 0  9990\n" +
                        "2 00902  90.1619  31.5795 0018818  17.0373  41.7246 13.52685257565000",
                "1 00900U 64063C   20202.17095681  .00000187  00000-0  19107-3 0  9998\n" +
                        "2 00900  90.1522  28.9147 0025875 322.1024 146.6777 13.73394820774941"
        };

        Processor processor = new TleProcessor("C:\\Users\\Administrator\\Desktop\\runtime\\tle1\\TLE_J2000KEPL.exe");
        Executor executor = new SimpleExecutor(1);
        executor.init(1, new Processor[]{processor});

        Element element = null;
        for (String param : params) {
            element = new Element();
            element.setParams(param);
            try {
                executor.execute(1, element);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
