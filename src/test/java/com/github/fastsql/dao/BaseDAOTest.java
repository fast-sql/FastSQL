package com.github.fastsql.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class BaseDAOTest {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    StudentDAO studentDao;


    @Before
    public void datasource() throws SQLException {

//        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("log4j.logger.org.springframework.jdbc.core.StatementCreatorUtils", "Trace");

        DataSource dataSource = new SimpleDriverDataSource(
                new com.mysql.jdbc.Driver(),
                "jdbc:mysql://localhost:3306/fastsql?characterEncoding=utf8&useSSL=true",
                "pig",
                "123456");
//        DataSource dataSource = new SimpleDriverDataSource(
//                new com.mysql.jdbc.Driver(),
//                "jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=true",
//                "root",
//                "123456");
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);


        studentDao = new StudentDAO();

        studentDao.setTemplate(namedParameterJdbcTemplate); //模拟注入

    }

    @Test
    public void save() throws NoSuchFieldException {
//        Field field = StatementCreatorUtils.class.getField("logger");
//        field.setAccessible(true);

        Student student = new Student();
        student.setId(new Random().nextInt(1234));
        student.setName("2132131");
        student.setBirthday(LocalDate.now());
        student.setHomeAddress("");
        student.setCityId("xxxxxxxxxx");

        studentDao.save(student);
    }

//    @Test
//    public void save21323() {
//
//    }

    @Test
    public void saveIgnoreNull() {

        Student student = new Student();
        String id = UUID.randomUUID().toString();
        System.out.println(id);
        student.setId(123234);
        student.setName("小丽");
        student.setBirthday(null);
//        student.setHomeAddress("");
        student.setCityId("2212");

        studentDao.saveIgnoreNull(student);


    }

    @Test
    public void delete() {
        int deleteRowNumber = studentDao.delete("b89de0c4-b517-4088-a109-40ffc3aa6d4a");
    }

    @Test
    public void deleteInBatch() {
        List<String> ids = new ArrayList<>();
        ids.add("264024f4-07d0-48b0-9249-b17545a6ad5a");
//        ids.add("6");
//        ids.add("5");

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
        dto.setBirthday(LocalDate.of(1991, 10, 10));
//        student.setBirthday(new Date());

        List<Student> studentList1 = studentDao.findListWhere(
                "name LIKE :name AND  birthday < :birthday ",
                new BeanPropertySqlParameterSource(dto)
        );
        System.out.println(studentList1);
    }

    static class StudentIndexDTO {
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
        student.setId(234);
        student.setName("99999");

        studentDao.update(student);
    }

    @Test
    public void updateIgnoreNull() {


        Student student = new Student();
        student.setId(234);
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


