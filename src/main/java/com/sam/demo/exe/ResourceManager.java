package com.sam.demo.exe;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Resource;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResourceManager<D extends Carrier> {

    private Map<Class, Resource> resources = new ConcurrentHashMap<>();

    private BlockingQueue<D> queue = new LinkedBlockingQueue<>();

    public void register(Class tClass, Resource resource) {
        if(resource.getClass().isAssignableFrom(tClass)){
            resources.put(tClass, resource);
        }
    }

    public Resource<D> takeResource(Class tClass) throws InterruptedException {
        Resource<D> resource = resources.get(tClass);
        resource.control();
        return resource;
    }

    public D getData() throws InterruptedException {
        return queue.poll(1000, TimeUnit.MILLISECONDS);
    }

    public Resource releaseResource(Class tClass) {
        Resource resource = resources.get(tClass);
        resource.release();
        return resource;
    }

    public boolean putData(D data) throws InterruptedException {
        return queue.offer(data, 1000, TimeUnit.MILLISECONDS);
    }

    public void waitQueue() {
        D peek;
        while (true) {
            peek = queue.peek();
            if(peek != null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            return;
        }
    }

    public void close() {
        resources.forEach((tCLass, resource)-> resource.close());
    }

}
