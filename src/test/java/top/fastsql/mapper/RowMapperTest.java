package top.fastsql.mapper;

import org.junit.Test;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static top.fastsql.TestConstants.myFactory;

/**
 * @author ChenJiazhi
 */
public class RowMapperTest {

    private static class StudentVO {
        private String name;
        private String name2;
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

        public String getName2() {
            return name2;
        }

        public void setName2(String name2) {
            this.name2 = name2;
        }
    }

    @Test
    public void testRowMapper() {
        myFactory.sql()
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
}
