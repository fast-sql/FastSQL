package top.fastsql.mapper;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Chenjiazhi
 * 2018-08-01
 */
public class CombinedBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {
    public CombinedBeanPropertyRowMapper() {
    }


    public CombinedBeanPropertyRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }


    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T t = super.mapRow(rs, rowNumber);

        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            InnerBeanMapped annotation = field.getAnnotation(InnerBeanMapped.class);
            if (annotation == null) {
                continue;
            }

            Class<?> fieldType = field.getType();

            BeanPropertyRowMapper<?> rowMapper = new BeanPropertyRowMapper<>(fieldType);
            Object instantiate = rowMapper.mapRow(rs, rowNumber);

            try {
                field.set(t, instantiate);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        return t;
    }


}
