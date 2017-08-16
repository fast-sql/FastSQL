package com.github.fastsql.util;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

/**
 * @author Jiazhi
 * @since 2017/8/9
 */
public class FastSqlTemplate {
    /** The JdbcTemplate we are wrapping */
    private final JdbcOperations classicJdbcTemplate;

    public FastSqlTemplate(JdbcOperations classicJdbcTemplate) {
        Assert.notNull(classicJdbcTemplate, "JdbcTemplate must not be null");
        this.classicJdbcTemplate = classicJdbcTemplate;
    }



}
