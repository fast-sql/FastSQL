package com.github.fastsql.dao;


import com.github.fastsql.dto.StudentIndexDTO;
import com.github.fastsql.dto.DbPageResult;
import com.github.fastsql.vo.StudentVO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jiazhi
 * @since 2017/4/2
 */
@Repository
public class StudentDAO extends BaseDAO<Student,String> {

    public List<StudentVO> findStudentVOList(StudentIndexDTO dto) {
        String sql = "SELECT s.*,c.name AS cityName FROM student s " +//template可以直接使用
                "LEFT JOIN city c ON s.city_id = c.id " +
                "WHERE s.age = :age AND c.name = :cityName ";//命名参数

        List<StudentVO> studentVOList = queryListBySql(sql,//命名参数
                new BeanPropertySqlParameterSource(dto), //传入参数***
                new BeanPropertyRowMapper<>(StudentVO.class));//匹配传出参数***
        return studentVOList;
    }


    /**
     * 查询前十条数据
     */
    public DbPageResult<StudentVO> findStudentVOPage(StudentIndexDTO dto) {
        String sql = "SELECT s.*,c.name AS cityName FROM student s " +//template可以直接使用
                "LEFT JOIN city c ON s.city_id = c.id " +
                "WHERE s.age = :age AND c.name = :cityName ";//命名参数

        DbPageResult<StudentVO> studentDbPageResult = queryPageBySql(
                sql, 1, 10,  //sql ,页数 ，和每页条数
                new BeanPropertySqlParameterSource(dto), //传入参数***
                new BeanPropertyRowMapper<>(StudentVO.class));//匹配传出参数***
        return studentDbPageResult;
    }
}
