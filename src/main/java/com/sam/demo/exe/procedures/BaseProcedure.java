package com.sam.demo.exe.procedures;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Resource;
import com.sam.demo.exe.ResourceManager;
import com.sam.demo.exe.resources.Executor;
import com.sam.demo.exe.impl.Reader;
import com.sam.demo.exe.impl.Writer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseProcedure<D extends Carrier> extends RunnerProcedure {

    private ResourceManager<D> manager;

    public void setManager(ResourceManager<D> manager) {
        this.manager = manager;
    }

    @Override
    protected void process() {
        D data;
        try {
            try {
                data = manager.getData();
                if(data == null) return;
            } catch (InterruptedException e) {
                return;
            }
            log.info("获得资源 {}", data);

            Resource<D> writer = manager.takeResource(Writer.class);
            writer.doSomething(data);
            log.info("writer完毕，可执行");

            Resource<D> executor = manager.takeResource(Executor.class);
            executor.doSomething(data);

            log.info("执行完毕，释放writer");
            writer.release();

            Resource<D> reader = manager.takeResource(Reader.class);
            reader.doSomething(data);
            reader.release();

            log.info("reader完毕，可再次执行，释放executor");
            executor.release();
            log.info("数据结果 {}", data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



    @Override
    public void destroy() {
        manager.waitQueue();
        super.destroy();
    }

    @Override
    protected void close() {
        manager.close();
    }

}
