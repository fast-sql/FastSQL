package com.github.fastsql.util;

/**
 * @author Jiazhi
 * @since 2017/3/30
 */
public class SQLBuilder {

    private StringBuilder sqlBuilder = new StringBuilder();


    public SQLBuilder() {
    }

    public SQLBuilder(String sql) {
        this.sqlBuilder.append(sql);
    }

    public SQLBuilder SELECT(String columns) {
        sqlBuilder.append("SELECT ")
                .append(columns);
        return this;
    }

    public SQLBuilder FROM(String table) {
        sqlBuilder.append(" FROM ")
                .append(table);
        return this;
    }

    public SQLBuilder LEFT_JOIN_ON(String table, String on) {
        sqlBuilder.append(" LEFT OUTER JOIN ")
                .append(table)
                .append(" ON ( ")
                .append(on)
                .append(") ");
        return this;
    }

    public SQLBuilder WHERE_1EQ1() {
        sqlBuilder.append(" WHERE 1=1 ");
        return this;
    }

    public SQLBuilder WHERE(String condtions) {
        sqlBuilder.append(" WHERE ( ")
                .append(condtions + ") ");
        return this;
    }

    public SQLBuilder AND_IF(String condition, Object object) {
        if (object == null || "".equals(object)) {
            return this;
        } else {
            sqlBuilder.append(" AND (")
                    .append(condition)
                    .append(") ");
            return this;
        }

    }

    public SQLBuilder AND(String condition) {
        sqlBuilder.append(" AND ( ")
                .append(condition)
                .append(") ");
        return this;
    }

    public SQLBuilder ORDER_BY(String condition) {
        sqlBuilder.append(" ORDER BY ( ")
                .append(condition)
                .append(") ");
        return this;
    }

    public SQLBuilder GROUP_BY(String condition) {
        sqlBuilder.append(" GROUP BY ( ")
                .append(condition)
                .append(") ");
        return this;
    }

    public SQLBuilder C() {
        sqlBuilder.append(" ( ");
        return this;
    }

    public SQLBuilder D() {
        sqlBuilder.append(" ( ");
        return this;
    }

    public SQLBuilder HAVING(String condition) {
        sqlBuilder.append(" HAVING ( ")
                .append(condition)
                .append(") ");
        return this;
    }

//    public String pageBuild(int pageNumber, int perPageSize) {
//        return PageSqlUtils.pageSql(sqlBuilder.toString(), pageNumber, perPageSize);
//    }


    public String build() {
        return sqlBuilder.toString();
    }


}
