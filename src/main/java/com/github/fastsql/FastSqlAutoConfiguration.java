package com.github.fastsql;

import com.github.fastsql.config.FastSqlInit;
import com.github.fastsql.config.FastSqlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 陈佳志
 */
@Configuration // 配置注解
@EnableConfigurationProperties(FastSqlProperties.class) // 开启指定类的配置
public class FastSqlAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(FastSqlAutoConfiguration.class);

    @Bean
    public FastSqlInit fastSqlInit() {
//        log.info("fastSqlInit");
//        System.setProperty("logging.level.org.springframework.jdbc.core.JdbcTemplate", "debug");
//        System.setProperty("logging.level.org.springframework.jdbc.core.StatementCreatorUtils", "trace");
        return new FastSqlInit();
    }

}
