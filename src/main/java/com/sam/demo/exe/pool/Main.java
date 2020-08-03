package com.sam.demo.exe.pool;

import com.sam.demo.exe.ResourceManager;
import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.impl.Reader;
import com.sam.demo.exe.impl.Writer;
import com.sam.demo.exe.procedures.BaseProcedure;
import com.sam.demo.exe.procedures.Procedure;
import com.sam.demo.exe.resources.Executor;
import com.sam.demo.exe.resources.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Main<D extends Carrier> {

    private volatile boolean running = false;

    private Procedure[] procedures;
    private ResourceManager<D> resourceManager = new ResourceManager<>();

    private ExecutorService pool;

    public Main() {
        this(3);
    }

    public Main(int threadCore) {
        this.procedures = new Procedure[threadCore];
        NamedThreadFactory factory = new NamedThreadFactory("procedure");
        this.pool = Executors.newCachedThreadPool(factory);
    }

    public void register(Resource<D> resource) {
        if(running)
            throw new IllegalStateException("server is running");

        resourceManager.register(resource.getClass(), resource);
    }

    public void addWork(D data) throws InterruptedException {
        if(!running)
            throw new IllegalStateException("server is not running");
        resourceManager.putData(data);
    }

    public void start(){
        running = true;
        BaseProcedure<D> baseProcedure;
        for (int i = 0; i< procedures.length;i++){
            baseProcedure = new BaseProcedure<>();
            baseProcedure.setManager(resourceManager);
            procedures[i] = baseProcedure;
            pool.execute(baseProcedure);
        }
    }

    public void close(){
        //关闭put，shutdown线程池
        running = false;
        pool.shutdown();
        //等待que完结，关闭自旋
        //线程结束，关闭资源
        for (int i = 0; i< procedures.length;i++){
            procedures[i].destroy();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        String[] params = new String[]{
                "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                        "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436",
                "1 00902U 64063E   20202.50348760  .00000030  00000-0  31316-4 0  9990\n" +
                        "2 00902  90.1619  31.5795 0018818  17.0373  41.7246 13.52685257565000",
                "1 00900U 64063C   20202.17095681  .00000187  00000-0  19107-3 0  9998\n" +
                        "2 00900  90.1522  28.9147 0025875 322.1024 146.6777 13.73394820774941"
        };

        Main<Carrier> carrierMain = new Main<>();
        Writer writer = new Writer("D:\\SourceCode\\runtime\\tle1\\TLE.txt");
        Reader reader = new Reader("D:\\SourceCode\\runtime\\tle1\\J2000KEPL.TXT");
        Executor<Carrier> executor = new Executor<>("D:\\SourceCode\\runtime\\tle1", "TLE_J2000KEPL.exe");
        carrierMain.register(writer);
        carrierMain.register(reader);
        carrierMain.register(executor);

        carrierMain.start();

        for (String param : params) {
            Carrier carrier = new Carrier();
            carrier.setUnique(UUID.randomUUID().toString());
            carrier.setWriterContent(param);
            carrierMain.addWork(carrier);
        }

        Thread.sleep(5000);
        carrierMain.close();
    }

}
