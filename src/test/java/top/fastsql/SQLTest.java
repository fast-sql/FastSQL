package top.fastsql;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import top.fastsql.config.DataSourceType;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static top.fastsql.TestConstants.myFactory;
import static top.fastsql.util.FastSqlUtils.listOf;


public class SQLTest {
    private Logger log = LoggerFactory.getLogger(SQLTest.class);


    /**
     * 测试byType()
     */
    @Test
    public void testByType() {
        myFactory.sql()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("age").lt().byType(10)
                .AND("name").eq().byType("小明")
                .build();
    }


    /**
     * 测试IN语句
     */
    @Test
    public void testIN() {
        myFactory.sql()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("name").IN(new Object[]{"小红", "小明"})
                .build();

        myFactory.sql()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("name").IN_var("小红", "小明")
                .build();
    }

    /**
     * 测试page=0的情况
     */
    @Test
    public void testPage0() {
        myFactory.setDataSourceType(DataSourceType.POSTGRESQL);
        System.out.println(myFactory.sql().SELECT("id").FROM("sys_dict").queryPage(0, 1, String.class));
    }

    /**
     * 测试PerPage=0的情况
     */
    @Test
    public void testPerPage0() {
        myFactory.setDataSourceType(DataSourceType.POSTGRESQL);
        System.out.println(myFactory.sql().SELECT("id").FROM("sys_dict").queryPage(1, 0, String.class));
    }
}
