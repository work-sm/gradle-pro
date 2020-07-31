package com.sam.demo.jmockit;

public class OuterClass {
    private InnerClass innerClass;

    public String getStr(String ss){
        return "hi "+ innerClass.getVal(ss);
    }
}
