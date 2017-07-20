package com.github.fastsql.util;

/**
 * @author Jiazhi
 * @since 2017/4/7
 */
public class PageSqlUtils {

    public static String DB_TYPE = "mysql";//mysql,postgrsql,oracle


//    public static SQLGroup getGroupSQL(String sql, int pageNumber, int perPageSize) {
//        SQLGroup twoSql = new SQLGroup();
//        twoSql.setCountSQL("SELECT COUNT(*) FROM ( " + sql + " )");
//        twoSql.setSelectSQL(pageSql(sql, pageNumber, perPageSize));
//        return twoSql;
//
//    }

//    public static String getPageFindSql(String sql, int pageNumber, int perPageSize) {
//        return pageSql(sql, (int) pageNumber, (int) perPageSize);
//    }

    public static String findSQL(String sql, int pageNumber, int perPageSize) {
        if (DB_TYPE.equals("mysql")) {
            return mysql(sql, pageNumber, perPageSize);
        } else if (DB_TYPE.equals("postgrsql")) {
            return postgrsql(sql, pageNumber, perPageSize);
        } else if (DB_TYPE.equals("oracle")) {
            return oracle(sql, pageNumber, perPageSize);
        } else {
            throw new RuntimeException("PageSqlUtils.DB_TYPE 错误");
        }
    }

    public static String countSQL(String sql) {
        return "SELECT COUNT(*) FROM ( " + sql + " )";
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
    public static String postgrsql(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int offset = perPageSize;

        return sql + " LIMIT " + limit + " OFFSET" + offset;
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
