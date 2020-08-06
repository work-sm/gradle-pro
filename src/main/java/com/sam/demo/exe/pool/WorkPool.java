package com.sam.demo.exe.pool;

import com.sam.demo.exe.ResourceManager;
import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.procedures.impl.BaseProcedure;
import com.sam.demo.exe.procedures.Procedure;
import com.sam.demo.exe.resources.Resource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkPool<D extends Carrier> {
    private volatile boolean running = false;

    private Procedure[] procedures;
    private ResourceManager<D> resourceManager = new ResourceManager<>();

    private ExecutorService pool;

    public WorkPool() {
        this(3);
    }

    public WorkPool(int threadCore) {
        this.procedures = new Procedure[threadCore];
        NamedThreadFactory factory = new NamedThreadFactory("procedure");
        this.pool = Executors.newCachedThreadPool(factory);
    }

    public void register(Resource resource) {
        if (running)
            throw new IllegalStateException("server is running");

        resourceManager.register(resource);
    }

    public void start() {
        running = true;
        BaseProcedure<D> baseProcedure;
        for (int i = 0; i < procedures.length; i++) {
            baseProcedure = new BaseProcedure<>();
            baseProcedure.setManager(resourceManager);
            procedures[i] = baseProcedure;
            pool.execute(baseProcedure);
        }
        //关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            close();
        }));
    }

    private void close() {
        //关闭put，shutdown线程池
        running = false;
        pool.shutdown();
        //等待que完结，关闭自旋
        //线程结束，关闭资源
        for (int i = 0; i < procedures.length; i++) {
            procedures[i].destroy();
        }
    }

    public void addWork(D data) throws InterruptedException {
        if (!running)
            throw new IllegalStateException("server is not running");
        resourceManager.putData(data);
    }

}
