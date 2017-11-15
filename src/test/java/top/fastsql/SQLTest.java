package top.fastsql;

import com.mysql.jdbc.Driver;
import org.junit.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import top.fastsql.config.DataSourceType;

import javax.sql.DataSource;
import java.sql.SQLException;

public class SQLTest {

    private static SQLFactory sqlFactory = new SQLFactory();

    static {
        DataSource dataSource = null;
        try {
            dataSource = new SimpleDriverDataSource(
                    new Driver(), "jdbc:postgresql://192.168.0.226:5432/picasso_dev2?stringtype=unspecified",
                    "developer", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sqlFactory.setDataSource(dataSource);
        sqlFactory.setDataSourceType(DataSourceType.MY_SQL);
    }

    @Test
    public void testOperatorMethod() {

    }

    /**
     * 测试byType()
     */
    @Test
    public void testByType() {
        sqlFactory.createSQL()
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
        sqlFactory.createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("name").IN(new Object[]{"小红", "小明"})
                .build();
    }

    @Test
    public void pageSQLTest() {
        sqlFactory.setLogSQLWhenBuild(true);

        sqlFactory.setDataSourceType(DataSourceType.ORACLE);
        sqlFactory.createSQL().SELECT("*").FROM("student").pageThis(1, 10).build();
    }


}
