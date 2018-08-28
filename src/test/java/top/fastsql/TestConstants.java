package top.fastsql;

import top.fastsql.config.DataSourceType;

import javax.sql.DataSource;

/**
 * @author ChenJiazhi
 */
public class TestConstants {
    /**
     * mysql
     */
    public static SQLFactory myFactory =
            SQLFactory.createUseSimpleDateSource("", "", "");


}
