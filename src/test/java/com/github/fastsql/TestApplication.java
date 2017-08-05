package com.github.fastsql;

import com.github.fastsql.dao.StudentDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
//        (scanBasePackageClasses = {StudentDAO.class}) //TODO
public class TestApplication {
    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public StudentDAO studentDAO() {
        return new StudentDAO();
    }
}
