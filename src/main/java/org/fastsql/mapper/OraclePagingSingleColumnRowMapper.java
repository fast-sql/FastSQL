package org.fastsql.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * oracle分页之后多了一列，这个类可以选取单列数据
 *
 * @author 陈佳志
 * 2017-10-25
 */
public class OraclePagingSingleColumnRowMapper<T> implements RowMapper<T> {
    private Class<?> requiredType;

    public OraclePagingSingleColumnRowMapper() {
    }

    public OraclePagingSingleColumnRowMapper(Class<?> requiredType) {
        this.requiredType = requiredType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return (T) rs.getObject(1, requiredType);
    }
}
