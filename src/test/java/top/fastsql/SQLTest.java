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

import static top.fastsql.util.FastSqlUtils.listOf;


public class SQLTest {

    private Logger log = LoggerFactory.getLogger(SQLTest.class);

    private static SQLFactory sqlFactory = new SQLFactory();

    static {
        DataSource dataSource = null;
        try {
//            dataSource = new SimpleDriverDataSource(
//                    new org.postgresql.Driver(), "jdbc:postgresql://192.168.0.226:5432/picasso_dev2?stringtype=unspecified",
//                    "developer", "password");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqlFactory.setDataSource(dataSource);
        sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL);


        //log

    }

    @Test
    public void testOperatorMethod() {
//        SimpleLoggerFactory factory = new SimpleLoggerFactory();
        log.info("222");
        log.debug("-----------");
        sqlFactory.createSQL()
                .SELECT("*")
                .FROM("sys_users")
                .queryMapList();
        Logger logger = LoggerFactory.getILoggerFactory().getLogger("top.fastsql.SQLTest");

        System.out.println("---");
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

    static class StudentVO {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Test
    public void testRowMapper() {
        sqlFactory.sql()
                .SELECT("name", "age")
                .FROM("student")
                .queryList(new RowMapper<StudentVO>() {
                    @Override
                    public StudentVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentVO studentVO = new StudentVO();
                        studentVO.setName(rs.getString("name"));
                        studentVO.setAge(rs.getInt("age"));
                        return studentVO;
                    }
                });
    }

    @Test
    public void testINCollection() {
        List<String> list = new ArrayList<>();
        list.add("小红");
        list.add("小红");

        String sql = sqlFactory.createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("name IN ?")
                .varParameter(list)
                .build();

        System.out.println(sql);

        List<Integer> list2 = new ArrayList<>();
        list2.add(1);
        list2.add(3);

        String sql2 = sqlFactory.createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("age IN ?")
                .varParameter(list2)
                .build();
        System.out.println(sql2);


    }

    @Test
    public void testINCollectionQuery() {


        sqlFactory.createSQL()
                .SELECT("name")
                .FROM("sys_dict")
                .WHERE("code IN ?")
                .varParameter(listOf("1"))
                .queryMapListAndPrint();
    }

    @Test
    public void testINCollectionDAOQuery() {
        SysDictDAO sysDictDAO = new SysDictDAO(sqlFactory);
        List<SysDict> list = sysDictDAO.selectWhere("code IN ? AND state=?", listOf("1", "abc"), 0);
        System.out.println(list);
    }

    @Test
    public void pageSQLTest() {
//        sqlFactory.setLogSQLWhenBuild(true);
        sqlFactory.setDataSourceType(DataSourceType.ORACLE);
        sqlFactory.createSQL().SELECT("*").FROM("student").pageThis(1, 10).build();
    }

    @Test
    public void testPage0() {
        sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL);
        System.out.println(sqlFactory.createSQL().SELECT("id").FROM("sys_dict").queryPage(0, 1, String.class));
    }

    @Test
    public void testPerPage0() {
        sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL);
        System.out.println(sqlFactory.createSQL().SELECT("id").FROM("sys_dict").queryPage(1, 0, String.class));
    }
}
