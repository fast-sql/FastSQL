package com.github.fastsql;

import com.github.fastsql.dao.Student;
import com.github.fastsql.dao.StudentDAO;
 import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    }
}
