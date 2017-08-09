package com.github.fastsql.util;

/**
 * @author 陈佳志
 */
public class PageSqlUtils {

    public static String DB_TYPE = "mysql";//mysql,postgrsql,oracle


    public static String getRowsSQL(String sql, int pageNumber, int perPageSize) {
        if (DB_TYPE.equals("mysql")) {
            return mysql(sql, pageNumber, perPageSize);
        } else if (DB_TYPE.equals("postgresql")) {
            return postgresql(sql, pageNumber, perPageSize);
        } else if (DB_TYPE.equals("oracle")) {
            return oracle(sql, pageNumber, perPageSize);
        } else {
            throw new RuntimeException("PageSqlUtils.DB_TYPE 错误");
        }
    }

    public static String getNumberSQL(String sql) {
        return "SELECT COUNT(*) FROM ( " + sql + " ) AS a ";
    }


    /**
     * @param pageNumber  页数，从第一页开始
     * @param perPageSize 每页条数，大于1
     */
    public static String mysql(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int offset = perPageSize;

        return sql + " LIMIT " + limit + "," + offset;
    }

    /**
     * @param pageNumber  页数，从第一页开始
     * @param perPageSize 每页条数，大于1
     */
    public static String postgresql(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int offset = perPageSize;

        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }

    /**
     * @param pageNumber  页数，从第一页开始
     * @param perPageSize 每页条数，大于1
     */
    public static String oracle(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int endRowNum = limit + perPageSize;

        return "SELECT * FROM " +
                " ( SELECT A.*, ROWNUM RN   FROM  " +
                " ( " + sql + " ) A   WHERE ROWNUM " +
                " <= " + endRowNum + ") WHERE RN >=  " + limit;
    }
}
