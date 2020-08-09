package com.sam.demo.perform.script;

import com.sam.demo.perform.actor.Actor;

public interface Script {
    void stepping();

    void accept(Actor actor) throws Exception;
}
