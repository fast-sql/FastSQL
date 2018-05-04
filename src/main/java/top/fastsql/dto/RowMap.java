package top.fastsql.dto;

import top.fastsql.util.FastSqlUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Chenjiazhi
 * 2018-04-05
 */
public class RowMap extends HashMap<String, Object> {

    public RowMap() {
    }

    public RowMap(Map<String, Object> map) {
        super(map);
    }

    /**
     * get Class Type
     */
    public Class<?> getType(String key) {
        return get(key).getClass();
    }

    public String getString(String key) {
        return (String) get(key);
    }

//    public <T, R> R get(String key, Function<T, R> function) {
//        return function.apply((T) get(key));
//    }
//
//    public static void main(String[] args) {
//        RowMap rowMap = new RowMap();
//        rowMap.put("name","1");
//
//        rowMap.get("name",  value->Integer.valueOf(value));
//    }

    public Integer getInteger(String key) {
        return (Integer) get(key);
    }


    public Date getDate(String key) {
        return (Date) get(key);
    }

    public LocalDate getLocalDate(String key) {
        return FastSqlUtils.toLocalDate(getDate(key));
    }

    public LocalDateTime getLocalDateTime(String key) {
        return FastSqlUtils.toLocalDateTime(getDate(key));
    }

    @SuppressWarnings("unchecked")
    public <CLS> CLS getValue(String key, Class<CLS> cls) {
        return (CLS) get(key);
    }


}
