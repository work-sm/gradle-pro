package com.sam.demo.neural.common.utils;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import com.sam.demo.neural.limiter.core.ClusterLimiter;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Stream Utils
 *
 * @author lry
 */
@Slf4j
public class StreamUtils {

    public static String loadScript(String name) {
        try {
            return CharStreams.toString(new InputStreamReader(
                    ClusterLimiter.class.getResourceAsStream(name), Charsets.UTF_8));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
