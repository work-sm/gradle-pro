package com.sam.demo.print;

import cn.hutool.core.io.FileUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfHold {
    private List<String> head = new ArrayList<>();
    private List<String> mdp = new ArrayList<>();
    private List<String> op = new ArrayList<>();
    private List<String> pod = new ArrayList<>();

    public void load() {
        int state = 0;
        List<String> lines = FileUtil.readLines("C:\\Users\\Administrator\\Desktop\\pan\\conf", "UTF-8");
        for (String line : lines) {
            if (line.contains("###################")) {
                state++;
            }
            if (state == 0) {
                head.add(line);
            } else if (state == 1) {
                mdp.add(line);
            } else if (state == 2) {
                pod.add(line);
            } else if (state == 3) {
                op.add(line);
            }
        }
    }

    public void write(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column annotation = field.getAnnotation(Column.class);
                String name = annotation.name();
                field.setAccessible(true);
                Object o = field.get(object);
                map.put(name, o);
            }
        }


    }
}
