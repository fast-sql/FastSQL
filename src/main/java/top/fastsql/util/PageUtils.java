package top.fastsql.util;

import top.fastsql.config.DatabaseType;

import java.util.Objects;

/**
 * @author 陈佳志
 */
public class PageUtils {

//    public static String getRowsSQL(String sql, int pageNumber, int perPageSize) {
//        //没指定dbType，使用FastSqlConfig配置项中默认的
//        return getRowsSQL(sql, pageNumber, perPageSize, FastSqlConfig.databaseType);
//    }


    public static String getRowsSQL(String sql, int pageNumber, int perPageSize, DatabaseType databaseType) {
        if (Objects.equals(databaseType, DatabaseType.MY_SQL)) {
            return mysql(sql, pageNumber, perPageSize);
        } else if (Objects.equals(databaseType, DatabaseType.POSTGRESQL)) {
            return postgresql(sql, pageNumber, perPageSize);
        } else if (Objects.equals(databaseType, DatabaseType.ORACLE)) {
            return oracle(sql, pageNumber, perPageSize);
        } else {
            throw new RuntimeException("不支持的数据库类型");
        }
    }



    public static String getNumberSQL(String sql) {
        // (T_T)
        //subQuery can not with  "AS"  in Oracle
        return "SELECT count(*) FROM ( " + sql + " ) total";
    }

    /**
     * @param pageNumber  页数，从第一页开始
     * @param perPageSize 每页条数，大于1
     */
    public static String mysql(String sql, int pageNumber, int perPageSize) {
        //偏移量，即是忽略offset行
        int offset = (pageNumber - 1) * perPageSize;
        return sql + " LIMIT " + offset + "," + perPageSize;
    }

    /**
     * @param pageNumber  页数，从第一页开始
     * @param perPageSize 每页条数，大于1
     */
    public static String postgresql(String sql, int pageNumber, int perPageSize) {
        //偏移量，即是忽略offset行
        int offset = (pageNumber - 1) * perPageSize;
        return sql + " LIMIT " + perPageSize + " OFFSET " + offset;
    }

    /**
     * @param pageNumber  页数，从第一页开始
     * @param perPageSize 每页条数，大于1
     */
    public static String oracle(String sql, int pageNumber, int perPageSize) {
        int limit = (pageNumber - 1) * perPageSize;
        int endRowNum = limit + perPageSize;

        return "SELECT * FROM" +
                "  (  " +
                "     SELECT t.*, ROWNUM RN FROM  ( " + sql + " ) t  WHERE ROWNUM  <= " + endRowNum +
                "   ) " +
                "WHERE RN >= " + limit;
    }
}
