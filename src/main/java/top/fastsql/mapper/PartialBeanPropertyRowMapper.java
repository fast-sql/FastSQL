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


//    public static Object getSuperField(Object paramClass, String paramString) {
//        Field field = null;
//        Object object = null;
//        try {
//            Class<?> superclass = paramClass.getClass().getSuperclass().getSuperclass();
//            field = superclass.getDeclaredField(paramString);
//            field.setAccessible(true);
//            object = field.get(paramClass);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return object;
//    }


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

    public void remainingMap(T object, ResultSet rs, int rowNumber) throws SQLException {

    }
}
