package com.sam.demo;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HystrixTest {

    private final HystrixCommand.Setter setter1 =
            HystrixCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("ExampleCommand"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter())
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExamplePool"))
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter());

    private final HystrixObservableCommand.Setter setter2 =
            HystrixObservableCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("ExampleCommand"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter());

    @Test
    public void test1() {
        HystrixRequestContext.initializeContext();

        HystrixCommand<String> command = new HystrixCommand<String>(setter1) {
            @Override
            protected String run() throws Exception {
                // 风险代码
                return "hi";
            }

            @Override
            protected String getFallback() {
                return "hi, sorry i am late...";
            }

            @Override
            protected String getCacheKey() {
                return "123";
            }
        };

        // execute() = queue().get()
        // String execute = hystrixCommand.execute();

        Future<String> future = command.queue();
        try {
            System.out.printf("exec command ... result = %s\n", future.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() throws InterruptedException {
        HystrixCommand<String> command = new HystrixCommand<String>(setter1) {
            @Override
            protected String run() throws Exception {
                return "hi";
            }

            @Override
            protected String getFallback() {
                return "hi, sorry i am late...";
            }
        };

        // observe() = toObservable().subscribe(subject)
        Observable<String> observe = command.observe();
        observe.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("completed...");
            }

            @Override
            public void onError(Throwable e) {
                System.out.printf("error...%s\n", e.getMessage());
            }

            @Override
            public void onNext(String s) {
                System.out.printf("from next...%s\n", s);
            }
        });

        Thread.sleep(2000);
//        observe.subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                System.out.println("==================call:" + s);
//            }
//        });
    }

    @Test
    public void test3() {
        HystrixCommand<String> command = new HystrixCommand<String>(setter1) {
            @Override
            protected String run() throws Exception {
                return "hi";
            }

            @Override
            protected String getFallback() {
                return "hi, sorry i am late...";
            }
        };

        //observe() = toObservable().subscribe(subject)
        Observable<String> observe = command.toObservable();
        BlockingObservable<String> blocking = observe.toBlocking();
        System.out.println(blocking.first());
    }

    @Test
    public void test4() {
        HystrixObservableCommand<String> command = new HystrixObservableCommand<String>(setter2) {
            @Override
            protected Observable<String> construct() {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> observer) {
                        try {
                            if (!observer.isUnsubscribed()) {
                                observer.onNext("123");
                                observer.onCompleted();
                            }
                        } catch (Exception e) {
                            observer.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }

            @Override
            protected Observable<String> resumeWithFallback() {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext("失败了！找大神来排查一下吧！");
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        };

        Observable<String> observe = command.observe();

//        observe.subscribe(new Observer<String>() {
//            @Override
//            public void onCompleted() {
//                System.out.println("completed...");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.printf("error...%s\n", e.getMessage());
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.printf("from next...%s\n", s);
//            }
//        });
        Iterator<String> iterator = observe.toBlocking().getIterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

}
