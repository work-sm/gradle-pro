package com.sam.demo.exe.impl;

import com.sam.demo.exe.ResourceManager;
import com.sam.demo.exe.impl.data.Carrier;
import com.sam.demo.exe.pool.ThreadPoolHold;
import com.sam.demo.exe.procedures.BaseProcedure;
import com.sam.demo.exe.procedures.Procedure;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ResourceManager<Carrier> resourceManager = new ResourceManager<>();
        resourceManager.register(Writer.class, new Writer());
        resourceManager.register(Reader.class, new Reader());
        resourceManager.register(Executor.class, new Executor());

        Procedure[] procedures = new Procedure[3];
        IntStream.of(0, 1, 2).forEach(i->{
            log.info("i {}", i);
            BaseProcedure<Carrier> baseProcedure = new BaseProcedure<>();
            baseProcedure.setManager(resourceManager);
            ThreadPoolHold.getInstance().execute(baseProcedure);
            procedures[i] = baseProcedure;
        });
        ThreadPoolHold.getInstance().shutdown();

        resourceManager.putData(new Carrier("world"));
        resourceManager.putData(new Carrier("hello"));
        resourceManager.putData(new Carrier("sam"));

        //关闭put，shutdown线程池

        //等待que完结，关闭自旋
        //线程结束，关闭资源
        IntStream.of(0, 1, 2).forEach(i->{
            procedures[i].destroy();
        });
    }

}
