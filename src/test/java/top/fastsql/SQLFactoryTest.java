package top.fastsql;

import org.junit.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

public class SQLFactoryTest {

    @Test
    public void crateFactory() {
        DataSource dataSource = new SimpleDriverDataSource();

        SQLFactory sqlFactory = new SQLFactory();
        sqlFactory.setDataSource(dataSource);
    }

    @Test
    public void crateSQL() {
        DataSource dataSource = new SimpleDriverDataSource();

        SQLFactory sqlFactory = new SQLFactory();
        sqlFactory.setDataSource(dataSource);

        SQL sql = sqlFactory.createSQL();
//        Student student = sql.SELECT("*").FROM("student").WHERE("id=101").queryOne(Student.class);
    }
}
