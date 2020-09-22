package com.sam.demo.perform.service.tle;

import com.sam.demo.perform.Director;
import com.sam.demo.perform.actor.Action;
import com.sam.demo.perform.actor.Actor;
import com.sam.demo.perform.actor.impl.SleepWaitExecutor;
import com.sam.demo.perform.clock.Clock;
import com.sam.demo.perform.clock.SimpleClock;

import java.util.UUID;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] params = new String[]{
                "1 40908U 15049K   20198.78459397  .00000322  00000-0  21304-4 0  9993\n" +
                        "2 40908  97.4917 197.7237 0014229 282.1605 158.5111 15.14062358266436",
                "1 00902U 64063E   20202.50348760  .00000030  00000-0  31316-4 0  9990\n" +
                        "2 00902  90.1619  31.5795 0018818  17.0373  41.7246 13.52685257565000",
                "1 00900U 64063C   20202.17095681  .00000187  00000-0  19107-3 0  9998\n" +
                        "2 00900  90.1522  28.9147 0025875 322.1024 146.6777 13.73394820774941"
        };

        Clock clock = new SimpleClock();
        // pc 唯一
        Actor exe = new SleepWaitExecutor("C:\\runtime\\tle0\\bin", "TLE_J2000KEPL", 300);
        Actor reader = new Reader("C:\\runtime\\tle0\\bin\\J2000KEPL.TXT", "reader");
        Actor writer = new Writer("C:\\runtime\\tle0\\bin\\TLE.txt", "writer");
        Director director = new Director(clock);
        director.register(exe);
        director.register(reader);
        director.register(writer);

        for(String param:params){
            TleStory story = new TleStory();
            story.setParam(param);
            story.setScene("tle");
            story.setUnique(UUID.randomUUID().toString());
            story.write("writer", Action.INVITE, "写参数");
            story.write("TLE_J2000KEPL", Action.INVITE, "执行脚本");
            story.write("writer", Action.RELEASE);
            story.write("reader", Action.INVITE, "读返回");
            story.write("reader", Action.RELEASE);
            story.write("TLE_J2000KEPL", Action.RELEASE);
            director.work(story);
        }

        // not work
        director.destroy();
        System.out.println("ok");
    }
}
