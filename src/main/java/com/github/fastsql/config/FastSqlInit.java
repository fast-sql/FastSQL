package com.github.fastsql.config;

import com.github.fastsql.util.PageSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Jiazhi
 * @since 2017/7/11
 */
public class FastSqlInit implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandLineRunner.class);

    @Autowired
    private FastSqlProperties fastSqlProperties;
//    @Autowired
//    private JdbcTemplate jdbcTemplate;//TODO

    @Override
    public void run(String... args) throws Exception {

         log.info("FastSqlInit");
//
//        if (fastSqlProperties.getShowSql()) {
//            log.info("-----show sql");
//            Field[] fields = jdbcTemplate.getClass().getDeclaredFields();
//            for (Field field : fields) {
//                System.out.println(field);
//            }
//
//
//            System.setProperty("logging.level.org.springframework.jdbc.core.JdbcTemplate", "debug");
//            System.setProperty("logging.level.org.springframework.jdbc.core.StatementCreatorUtils", "trace");
//        }
        PageSqlUtils.DB_TYPE = fastSqlProperties.getDbType();
    }
}
