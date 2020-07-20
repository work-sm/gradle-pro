package com.sam.demo.Test;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 观察者 Observer/Subscriber
 * 被观察者 Observable
 * 订阅 subscribe
 */
public class Rxjava {

    /**
     * 简单
     */
    @Test
    public void test1() {
        //观察者
        Observer<String> observer = new Observer<String>() {
            public void onCompleted() {
                System.out.println("observer onCompleted");
            }

            public void onError(Throwable e) {
                System.out.println("observer onError");
            }

            public void onNext(String s) {
                System.out.println("observer onNext " + s);
            }
        };
        Subscriber<String> subscriber = new Subscriber<String>() {
            public void onCompleted() {
                System.out.println("subscriber onCompleted");
            }

            public void onError(Throwable e) {
                System.out.println("subscriber onError");
            }

            public void onNext(String s) {
                System.out.println("subscriber onNext " + s);
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("observable");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(observer);
        observable.subscribe(subscriber);
        System.out.println("====================");

        //下面两种 observable 只能被订阅一次
        observable = Observable.just("Hello", "Hi", "Aloha");
        observable.subscribe(observer);
        observable.subscribe(subscriber);
        System.out.println("====================");

        String[] words = {"Hello", "Hi", "Aloha"};
        observable = Observable.from(words);
        observable.subscribe(observer);
        observable.subscribe(subscriber);
    }

    /**
     * 散装观察者
     * Obseravble 创建
     * just
     * from
     * create 实际过程
     * defer 声明过程
     *
     * range 随机数
     * interval 重复
     * timer 定时
     *
     * empty 直接完成
     * never 什么也不做
     * error 错误信号
     */
    @Test
    public void test2() {
        Action1<String> onNextAction = new Action1<String>() {
            public void call(String s) {
                System.out.println("onNext " + s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            public void call(Throwable throwable) {
                System.out.println("onError");
            }
        };
        Action0 onCompletedAction = new Action0() {
            public void call() {
                System.out.println("onCompleted");
            }
        };

        Observable<String> observable = Observable.just("Hello", "Hi", "Aloha");
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);

        Observable<String> never = Observable.never();

        never.subscribe(
                v -> System.out.println("This should never be printed!"),
                error -> System.out.println("Or this!"),
                () -> System.out.println("This neither!"));
    }

    /**
     * Schedulers.immediate(): 在当前线程运行，默认的 Scheduler
     * Schedulers.newThread(): 在新线程执行操作
     * Schedulers.io(): I/O 操作，线程池，可以重用空闲的线程
     * Schedulers.computation(): CPU 密集型计算
     *
     * map
     * flatMap
     * concatMap
     * filter
     * toList
     */
    @Test
    public void test3() throws InterruptedException {
        Observable.create(new Observable.OnSubscribe<String>() {
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("213");
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                //转换传输信息的类型
                .map(new Func1<String, Integer>() {
                    public Integer call(String filePath) {
                        return Integer.parseInt(filePath);
                    }
                })
                .doOnSubscribe(new Action0() {
                    public void call() {
                        System.out.println("初始");
                    }
                })
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                //转换传输信息为过程
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(Integer student) {
                        return Observable.from(new String[]{(student*1000)+"", "OK"});
                    }
                })
                .observeOn(Schedulers.immediate())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String integer) {
                        System.out.println(integer);
                    }
                });
        Thread.sleep(2000);
    }

    @Test
    public void test4 (){
        //被观察者
        Observable<String> observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                // 先定义过程
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("事件订阅开始");
                        subscriber.onCompleted();
                    }
                });
            }
        });

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            public void call(Throwable throwable) {
                System.out.println("onError");
            }
        };
        Action0 onCompletedAction = new Action0() {
            public void call() {
                System.out.println("onCompleted");
            }
        };

        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("观察者2订阅事件    " + s);
            }
        }, onErrorAction, onCompletedAction);
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("观察者1订阅事件    " + s);
            }
        }, onErrorAction, onCompletedAction);
    }
}
