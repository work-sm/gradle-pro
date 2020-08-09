package com.sam.demo.perform.util;

import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    private static final String pattern = "[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]).[0-9]{1,6}";
    private static final Pattern r = Pattern.compile(pattern);
    private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 时间 utc转北京
     *
     * @param lineContent 行内容
     * @return 替换后的行
     */
    public static String replaceTime(String lineContent) {
        String content = lineContent;
        Matcher m = r.matcher(lineContent);
        List<String> timeList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        while (m.find()) {
            stringList.add(m.group());
            long bjt = DateUtil.parse(m.group(), content).getTime() + (8 * 3600 * 1000);
            String time = DateUtil.format(new Date(bjt), format);
            timeList.add(time);
        }
        for (int i = 0; i < timeList.size(); i++) {
            content = content.replaceAll(stringList.get(i), timeList.get(i));
        }
        return content;
    }

}
