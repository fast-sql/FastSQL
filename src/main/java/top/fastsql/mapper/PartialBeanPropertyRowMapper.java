package top.fastsql.mapper;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 部分匹配
 *
 * @author Chenjiazhi
 * 2018-08-01
 */
public class PartialBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {
    public PartialBeanPropertyRowMapper() {
    }


    public PartialBeanPropertyRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }


    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T t = super.mapRow(rs, rowNumber);
        try {
            remainingMap(t, rs, rowNumber);
        } catch (SQLException e) {
            logger.error("", e);
        }

        return t;
    }

    /**
     * 子类需要重写这个方法
     *
     * @throws SQLException ex
     */
    public void remainingMap(T object, ResultSet rs, int rowNumber) throws SQLException {
        //Override
    }
}
