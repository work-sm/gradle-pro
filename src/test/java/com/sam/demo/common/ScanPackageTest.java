package com.sam.demo.common;

import com.sam.demo.nerver.common.ScanPackage;

import java.util.Set;

public class ScanPackageTest {
    public static void main(String[] args) {
        Set<Class<?>> classes = ScanPackage.getClasses("com.sam.demo.common");
        System.out.println(classes);
    }
}
