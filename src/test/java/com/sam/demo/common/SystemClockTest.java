package com.sam.demo.common;

import com.sam.demo.nerver.common.SystemClock;

public class SystemClockTest {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            SystemClock.now();
        }
        long end = System.currentTimeMillis();
        System.out.println("SystemClock Time:" + (end - start) + "毫秒");

        long start2 = System.currentTimeMillis();
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            System.currentTimeMillis();
        }
        long end2 = System.currentTimeMillis();
        System.out.println("currentTimeMillis Time:" + (end2 - start2) + "毫秒");
    }
}
