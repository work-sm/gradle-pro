package com.sam.demo;

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

    @Test
    public void test1() {
        //观察者
        Observer<String> observer = new Observer<String>() {
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            public void onError(Throwable e) {
                System.out.println("onError");
            }

            public void onNext(String s) {
                System.out.println("onNext " + s);
            }
        };
        Subscriber<String> subscriber = new Subscriber<String>() {
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            public void onError(Throwable e) {
                System.out.println("onError");
            }

            public void onNext(String s) {
                System.out.println("onNext " + s);
            }
        };

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("业务");
                subscriber.onCompleted();
            }
        });

        observable.subscribe(observer);

        observable = Observable.just("Hello", "Hi", "Aloha");
        observable.subscribe(subscriber);

        String[] words = {"Hello", "Hi", "Aloha"};
        observable = Observable.from(words);
        //只能调一次
        observable.subscribe(observer);
        observable.subscribe(subscriber);
    }

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

        String[] words = {"Hello", "Hi", "Aloha"};
        Observable<String> observable = Observable.from(words);
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    /**
     * Schedulers.immediate(): 在当前线程运行，默认的 Scheduler
     * Schedulers.newThread(): 在新线程执行操作
     * Schedulers.io(): I/O 操作，线程池，可以重用空闲的线程
     * Schedulers.computation(): CPU 密集型计算
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
                //concatMap
                //filter
                //toList
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

    public static void main(String[] args) {
        //被观察者
        Observable<String> observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                // 先定义过程
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("事件订阅开始");
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

        //订阅事件1，没产生一个订阅就会生成一个新的observable对象
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("观察者2订阅事件    " + s);
            }
        }, onErrorAction, onCompletedAction);
        //订阅事件2，没产生一个订阅就会生成一个新的observable对象
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("观察者1订阅事件    " + s);
            }
        }, onErrorAction, onCompletedAction);
    }
}
