package com.github.fastsql.util;

/**
 * @author Jiazhi
 * @since 2017/7/11
 */

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {
    String value();
}
