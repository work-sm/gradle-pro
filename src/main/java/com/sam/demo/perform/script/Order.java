package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Order {
    private String actor;
    private Action action;
    private String desc;
}
