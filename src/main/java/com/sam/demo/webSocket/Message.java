package com.sam.demo.webSocket;

import lombok.Data;

@Data
public class Message {
    private Integer type;
    private Integer loop;
    private String target;
    private String name;
    private String msg;
}
