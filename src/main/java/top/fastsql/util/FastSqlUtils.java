package top.fastsql.util;

import java.time.*;
import java.util.*;

/**
 * 内部工具类
 *
 * @author 陈佳志
 * @author 袁臻
 */
public class FastSqlUtils {

    /**
     * 根据列表获取IN语句
     * eg.  1,2,3 返回 （1,2,3）
     * eg.  "1","q","u" 返回 （'1','q','u'）
     *
     * @param collection 列表
     * @return IN语句
     */
    public static String getInClause(Collection<?> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("getInClause参数不能为null");
        }
        if (collection.size() < 1) {
            return "()";
        }

        StringBuilder builder = new StringBuilder("(");
        Object firstValue = collection.iterator().next();
        if (firstValue instanceof Integer) {
            for (Object o : collection) {
                appendNoQuotes(builder, o);
            }
        } else if (firstValue instanceof String) {
            for (Object o : collection) {
                appendWithQuotes(builder, o);
            }
        } else if (firstValue instanceof Double) {
            for (Object o : collection) {
                appendNoQuotes(builder, o);
            }
        } else if (firstValue instanceof Float) {
            for (Object o : collection) {
                appendNoQuotes(builder, o);
            }
        } else if (firstValue instanceof Long) {
            for (Object o : collection) {
                appendNoQuotes(builder, o);
            }
        } else {
            for (Object o : collection) {
                appendWithQuotes(builder, o);
            }
        }
        builder.append(")");
        return builder.toString().replaceFirst(",", "");
    }

    private static void appendWithQuotes(StringBuilder builder, Object value) {
        builder.append(",'").append(value).append("'");
    }

    private static void appendNoQuotes(StringBuilder builder, Object value) {
        builder.append(",").append(value);
    }

    /**
     * 内存分页
     * page 1开始
     * size >0
     *
     * @param list 列表
     */
    public static <T> List<T> memoryPage(List<T> list, Integer page, Integer size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 0) {
            size = 0;
        }

        Integer from = (page - 1) * size;
        if (from > list.size()) {
            return new ArrayList<T>();
        }

        Integer to = (page - 1) * size + size;
        if (to > list.size()) {
            to = list.size();
        }
        return list.subList(from, to);

    }

    /**
     * 返回源字符串的左端添加通配符后的字符串
     *
     * @param source 源字符串
     * @return 源字符串的左端添加通配符后的字符串
     */
    public static String leftWildcard(final String source) {
        return String.format("%%%s", source);
    }

    /**
     * 返回源字符串的右端添加通配符后的字符串
     *
     * @param source 源字符串
     * @return 源字符串的右端添加通配符后的字符串
     */
    public static String rightWildcard(final String source) {
        return String.format("%s%%", source);
    }

    /**
     * 返回源字符串的两端添加通配符后的字符串
     *
     * @param source 源字符串
     * @return 源字符串的两端添加通配符后的字符串
     */
    public static String bothWildcard(final String source) {
        return String.format("%%%s%%", source);
    }


    public static <A> List<A> listOf(A... elements) {
        List<A> list = new ArrayList<>();
        list.addAll(Arrays.asList(elements));
        return list;
    }

    /**
     * java.util.Date --> java.time.LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(java.util.Date date) {
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
    public static LocalDate toLocalDate(Date date) {
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
    public static LocalTime toLocalTime(Date date) {
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
    public static Date toUtilDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * java.time.LocalDate --> java.util.Date
     */
    public static Date toUtilDate(LocalDate localDate) {
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
    public static Date toUtilDate(LocalTime localTime) {
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