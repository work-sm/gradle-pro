package com.sam.demo.exe;

import com.sam.demo.exe.data.Carrier;
import com.sam.demo.exe.resources.Doers;
import com.sam.demo.exe.resources.Resource;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResourceManager<D extends Carrier> {

    private Map<String, Resource> resources = new ConcurrentHashMap<>();

    private BlockingQueue<D> queue = new LinkedBlockingQueue<>();

    public void register(Resource resource) {
        if(Doers.class.isAssignableFrom(resource.getClass())){
            String name = ((Doers) resource).name();
            resources.put(name, resource);
        }
    }

    public Resource takeResource(String name) throws InterruptedException {
        Resource resource = resources.get(name);
        resource.control();
        return resource;
    }

    public D getData() throws InterruptedException {
        return queue.poll(1000, TimeUnit.MILLISECONDS);
    }

    public Resource releaseResource(String name) {
        Resource resource = resources.get(name);
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
