package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author masx
 *
 * <p> 单步描述
 */
@Getter
@Setter
@ToString
@Builder
public class Order {
    /**
     * 参演演员名
     */
    private String actor;
    /**
     * 演出动作
     */
    private Action action;
    /**
     * 动作描述
     */
    private String desc;
}
