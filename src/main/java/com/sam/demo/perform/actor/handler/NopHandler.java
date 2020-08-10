package com.hlht.mgt.perform.actor.handler;

public class NopHandler implements CommandHandler {
    @Override
    public String handle(String command) {
        return command;
    }
}
