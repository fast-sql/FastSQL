package com.github.fastsql;

import com.github.fastsql.dao.Student;
import com.github.fastsql.dao.StudentDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Jiazhi
 * @since 2017/8/5
 */
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = TestApplication.class)
@JdbcTest
@ContextConfiguration(classes = TestApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class MainTest {

    @Autowired
    StudentDAO studentDAO;

    @Test
    public void mainTest() {
        System.out.println("111");
        List<Student> students = studentDAO.findList();

        studentDAO.findListWhere("name LIKE ? AND age IS NULL", "%2%");


    }

    @Test
    public void save() {
        Student student = new Student();
        student.setId(UUID.randomUUID().toString());
        student.setName("小李");
        student.setAge(12);
        student.setBirthday(new Date());
        student.setHomeAddress("成都市天府二街");
        student.setCityId("12313");
        studentDAO.save(student);


        System.out.println("-----------------------------------");
        student.setName("儿子");
        studentDAO.update(student);
    }
}
