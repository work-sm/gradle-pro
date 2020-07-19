package com.sam.demo.nerver.common.spi;

import java.lang.annotation.*;

/**
 * 扩展点接口的标识。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * 缺省扩展点名。
     */
	String value() default "";

}