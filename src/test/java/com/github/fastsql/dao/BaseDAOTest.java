package com.github.fastsql.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BaseDAOTest {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    StudentDAO studentDao;


    @Before
    public void datasource() throws SQLException {

        System.setProperty("log4j.logger.org.springframework.jdbc.core.StatementCreatorUtils", "Trace");

//        DataSource dataSource = new SimpleDriverDataSource(
//                new com.mysql.jdbc.Driver(),
//                "jdbc:mysql://localhost:3306/fastsql?characterEncoding=utf8&useSSL=true",
//                "pig",
//                "123456");
        DataSource dataSource = new SimpleDriverDataSource(
                new com.mysql.jdbc.Driver(),
                "jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=true",
                "root",
                "123456");
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);


        studentDao = new StudentDAO();

        studentDao.setTemplate(namedParameterJdbcTemplate); //模拟注入

    }

    @Test
    public void save() {
        Student student = new Student();
        student.setId(UUID.randomUUID().toString());
        student.setName("小xxxxx");
        student.setBirthday(LocalDate.now());
        student.setHomeAddress("");

        studentDao.save(student);
    }

//    @Test
//    public void save21323() {
//
//    }

    @Test
    public void saveIgnoreNull() {

        Student student = new Student();
        student.setId(UUID.randomUUID().toString());
        student.setName("小丽");
        student.setBirthday(null);
        student.setHomeAddress("");

         studentDao.saveIgnoreNull(student);


     }

    @Test
    public void delete() {


        int deleteRowNumber = studentDao.delete("22b66bcf-1c2e-4713-b90d-eab17182b565");

    }

    @Test
    public void deleteInBatch() {
        List<String> ids = new ArrayList<>();
        ids.add("467641d2-e344-45e9-9e0e-fd6152f80867");
        ids.add("6");
        ids.add("5");

        System.out.println(studentDao.deleteInBatch(ids));
    }

    @Test
    public void deleteAll() {


        System.out.println(studentDao.deleteAll());
    }


    @Test
    public void findOne() {

        Student student = studentDao.findOne("12345678");
        System.out.println(student);

    }

    @Test
    public void findOneWhere() {

        Student student = studentDao.findOneWhere("name=?1 AND home_address=?2", "小明", "成都");
        System.out.println(student);

    }

    @Test
    public void findListWhere1() {
        List<Student> studentList1 =
                studentDao.findListWhere(
                        "name LIKE ?1 OR home_address IS NULL ORDER BY age DESC", "%明%");
        System.out.println(studentList1);
    }

    @Test
    public void findListWhere2() {

        StudentIndexDTO dto = new StudentIndexDTO();
        dto.setName("%小%");
        dto.setBirthday(LocalDate.of(1991,10,10));
//        student.setBirthday(new Date());

        List<Student> studentList1 = studentDao.findListWhere(
                "name LIKE :name AND  birthday < :birthday ",
                new BeanPropertySqlParameterSource(dto)
        );
        System.out.println(studentList1);
    }

    static class StudentIndexDTO{
        private String name;
        private LocalDate birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDate getBirthday() {
            return birthday;
        }

        public void setBirthday(LocalDate birthday) {
            this.birthday = birthday;
        }
    }

    @Test
    public void findListWhere3() {


        Map<String, Object> map = new HashMap<>();
        map.put("name", "%小%");
        map.put("birthday", new Date());

//        student.setName(");
//        student.setBirthday(new Date());

        List<Student> studentList1 = studentDao.findListWhere(
                "name LIKE :name AND  ( birthday < :birthday OR home_address IS NULL)",
                map
        );
        System.out.println(studentList1);
    }


    @Test
    public void countWhere() {


        int countWhere = studentDao.countWhere("age >= 20");

        System.out.println(countWhere);


    }

    @Test
    public void update() {


        Student student = new Student();
        student.setId("17661a16-e77b-4979-8a25-c43a489d42ad");
        student.setName("99999");

        studentDao.update(student);


    }

    @Test
    public void updateIgnoreNull() {


        Student student = new Student();
        student.setId("17661a16-e77b-4979-8a25-c43a489d42ad");
        student.setName("99999");

        studentDao.updateIgnoreNull(student);


    }



    @Test
    public void findVO() {
//        StudentIndexDTO dto = new StudentIndexDTO();
//        dto.setAge(10);
//        dto.setCityName("成都");
//
//        List<StudentVO> studentVOList = studentDao.findStudentVOList(dto);

    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
    }


}


