package com.sam.demo.jmockit;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.PrintStream;

@RunWith(JMockit.class)
public class JMockitTest {

    // @Mocked 改变类行为
    // @Injectable 改变实例行为
    @Injectable
    private InnerClass innerClass;

    @Mocked
    private PrintStream out;

    // @Tested 测试对象
    @Tested
    private OuterClass outerClass;

    @Test
    public void test(){
        String ss = "class";
        String ss1 = "class";
        new Expectations() {
            {
                innerClass.getVal(anyString);
                result = "hello || ss";
            }
        };
        String msg = outerClass.getStr(ss);
        System.out.println(msg);
    }

    @Test
    public <T extends AnOrdinaryInterface> void testMockUp() {
        // 通过传给MockUp一个泛型类型变量，MockUp可以对这个类型变量的上限进行Mock，以后所有这个上限的方法调用，就会走Mock后的逻辑
        new MockUp<T>() {
            @Mock
            public int method1() {
                return 10;
            }

            @Mock
            public int method2() {
                return 20;
            }
        };
        // 自定义一个AnOrdinaryInterface的实现
        AnOrdinaryInterface instance1 = new AnOrdinaryInterface() {
            @Override
            public int method1() {
                return 1;
            }

            @Override
            public int method2() {
                return 1;
            }
        };
        // 再定义一个AnOrdinaryInterface的实现
        AnOrdinaryInterface instance2 = new AnOrdinaryInterface() {
            @Override
            public int method1() {
                return 2;
            }

            @Override
            public int method2() {

                return 2;
            }
        };
        // 发现自定义的实现没有被作用，而是被Mock逻辑替代了
        Assert.assertTrue(instance1.method1() == 10);
        Assert.assertTrue(instance2.method1() == 10);
        Assert.assertTrue(instance1.method2() == 20);
        Assert.assertTrue(instance2.method2() == 20);
    }

    interface AnOrdinaryInterface{
        int method1();
        int method2();
    }
}
