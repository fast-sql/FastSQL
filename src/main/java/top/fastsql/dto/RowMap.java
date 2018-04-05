package top.fastsql.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static top.fastsql.util.FastSqlUtils.toLocalDate;
import static top.fastsql.util.FastSqlUtils.toLocalDateTime;

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
     * get Type
     */
    public Class<?> getType(String key) {
        return get(key).getClass();
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Integer getInteger(String key) {
        return (Integer) get(key);
    }

    public LocalDateTime getLocalDateTime(String key) {
        Object data = get(key);
        if (data instanceof Date) {
            return toLocalDateTime((Date) data);
        } else {
            return (LocalDateTime) data;
        }
    }

    public LocalDate getLocalDate(String key) {
        Object data = get(key);
        if (data instanceof Date) {
            return toLocalDate((Date) data);
        } else {
            return (LocalDate) data;
        }
    }

    public Date getDate(String key) {
        return (Date) get(key);
    }

    @SuppressWarnings("unchecked")
    public <CLS> CLS getValue(String key, Class<CLS> cls) {
        return (CLS) get(key);
    }


}
