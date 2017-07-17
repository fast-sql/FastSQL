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

    public static String getPageFindSql(String sql, int pageNumber, int perPageSize) {
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

    public static String getPageCountSql(String sql) {
        return "SELECT COUNT(*) FROM ( " + sql + " )";
    }


    /**
     * @param pageNumber  1+
     * @param perPageSize 1+
     */
    public static String mysql(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int offset = perPageSize;

        return sql + " LIMIT " + limit + "," + offset;
    }

    public static String postgrsql(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int offset = perPageSize;

        return sql + " LIMIT " + limit + " OFFSET" + offset;
    }

    public static String oracle(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int endRowNum = limit + perPageSize;

        return "SELECT * FROM " +
                " ( SELECT A.*, ROWNUM RN   FROM  " +
                " ( " + sql + " ) A   WHERE ROWNUM " +
                " <= " + endRowNum + ") WHERE RN >=  " + limit;
    }

//    public static void main(String[] args) {
//        String sql = new SQLBuilder().SELECT("*").FROM("student").build();
//
//        System.out.println(PageSqlUtils.mysql(sql, 3, 4));
//    }

//    public static class SQLGroup{
//        private String countSQL;
//        private String selectSQL;
//
//
//        public String getCountSQL() {
//            return countSQL;
//        }
//
//        public void setCountSQL(String countSQL) {
//            this.countSQL = countSQL;
//        }
//
//        public String getSelectSQL() {
//            return selectSQL;
//        }
//
//        public void setSelectSQL(String selectSQL) {
//            this.selectSQL = selectSQL;
//        }
//    }

}
