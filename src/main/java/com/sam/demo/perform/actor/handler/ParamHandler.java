package com.sam.demo.perform.actor.handler;

import java.util.ArrayList;
import java.util.List;

public class ParamHandler implements CommandHandler {

    private List<String> params = new ArrayList<>();

    @Override
    public String handle(String command) {
        StringBuilder stringBuilder = new StringBuilder(command);

        stringBuilder.append(" ");
        for (String param: params){
            stringBuilder.append(param).append(" ");
        }

        return stringBuilder.toString();
    }

    public void addParam(String param){
        params.add(param);
    }

}
