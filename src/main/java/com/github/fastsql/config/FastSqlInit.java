package com.github.fastsql.config;

import com.github.fastsql.util.PageSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * @author 陈佳志
 */
public class FastSqlInit implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandLineRunner.class);

    @Autowired
    private FastSqlProperties fastSqlProperties;

    @Override
    public void run(String... args) throws Exception {

        log.info("FastSqlInit");

        PageSqlUtils.DB_TYPE = fastSqlProperties.getDbType();
    }
}
