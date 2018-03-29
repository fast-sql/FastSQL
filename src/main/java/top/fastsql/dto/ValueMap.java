package top.fastsql.dto;

import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chenjiazhi
 * 2018-03-29
 */
public class ValueMap extends HashMap<String, Object> {

    public ValueMap() {
    }

    public ValueMap(Map<String,Object> map) {

        super(map);
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

    public  Date getDate(String key) {
        return (Date) get(key);
    }

    @SuppressWarnings("unchecked")
    public <CLS> CLS getValue(String key, Class<CLS> cls) {
        return (CLS) get(key);
    }

    /**
     * java.util.Date --> java.time.LocalDateTime
     */
    private static LocalDateTime toLocalDateTime(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * java.util.Date --> java.time.LocalDate
     */
    private static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    /**
     * java.util.Date --> java.time.LocalTime
     */
    private static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }

    //////////////////////////////////////////////////////2///////////////////////////////////

    /**
     * java.time.LocalDateTime --> java.util.Date
     */
    private static Date toUtilDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * ava.time.LocalDate --> java.util.Date
     */
    private static Date toUtilDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * java.time.LocalTime --> java.util.Date
     */
    private static Date toUtilDate(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


}
