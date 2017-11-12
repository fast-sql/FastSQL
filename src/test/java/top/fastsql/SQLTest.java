package top.fastsql;

import org.junit.Test;
import top.fastsql.config.DataSourceType;

public class SQLTest {

    private static SQLFactory sqlFactory = new SQLFactory();

    @Test
    public void testOperatorMethod() {

    }

    @Test
    public void testByType() {
        sqlFactory.createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("age").lt().byType(10)
                .AND("name").eq().byType("小明")
                .build();
    }


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
        sqlFactory.createSQL().SELECT("*").FROM("student").pageThis(1,10).build();
    }


}
