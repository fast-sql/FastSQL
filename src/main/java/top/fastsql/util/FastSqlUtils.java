package top.fastsql.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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


}