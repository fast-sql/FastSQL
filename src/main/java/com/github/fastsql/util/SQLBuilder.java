package com.github.fastsql.util;

import com.google.common.base.Joiner;

/**
 * @author 陈佳志
 */
public class SQLBuilder {

    private StringBuilder sqlBuilder = new StringBuilder();


    public SQLBuilder() {
    }

    public SQLBuilder(String sql) {
        this.sqlBuilder.append(sql);
    }

    public SQLBuilder contact(String string) {
        sqlBuilder.append(string + " ");
        return this;
    }


    public SQLBuilder SELECT(String... columns) {
        String columnsStr = Joiner.on(",").join(columns);
        sqlBuilder.append("\nSELECT ").append(columnsStr).append("\n");
        return this;
    }

    public SQLBuilder FROM(String tableName) {
        sqlBuilder.append("FROM ").append(tableName).append("\n");
        return this;
    }

    public SQLBuilder LEFT_JOIN_ON(String table, String on) {
        sqlBuilder.append("LEFT OUTER JOIN ")
                .append(table)
                .append(" ON ( ")
                .append(on)
                .append(" ) \n");
        return this;
    }

//    public SQLBuilder WHERE_1EQ1() {
//        sqlBuilder.append(" WHERE 1=1 ");
//        return this;
//    }

    public SQLBuilder WHERE() {
        sqlBuilder.append("WHERE 1=1 \n");
        return this;
    }

    public SQLBuilder WHERE(String condtions) {
        sqlBuilder.append("WHERE ").append(condtions).append("\n");
        return this;
    }

    public SQLBuilder IF_PRESENT_AND(Object object, String condition) {
        if (object == null || "".equals(object)) {
            return this;
        } else {
            sqlBuilder.append("AND ").append(condition).append(" \n");
            return this;
        }

    }

    public SQLBuilder AND(String condition) {
        sqlBuilder.append("AND ")
                .append(condition)
                .append("\n");
        return this;
    }

    public SQLBuilder OR(String condition) {
        sqlBuilder.append("OR ")
                .append(condition)
                .append(" \n");
        return this;
    }

    public SQLBuilder ORDER_BY(String condition) {
        sqlBuilder.append("ORDER BY ")
                .append(condition)
                .append(" \n");
        return this;
    }

    public SQLBuilder GROUP_BY(String condition) {
        sqlBuilder.append("GROUP BY ")
                .append(condition)
                .append("\n");
        return this;
    }

    public SQLBuilder L_BRACE() {
        sqlBuilder.append(" ( ");
        return this;
    }

    public SQLBuilder R_BRACE() {
        sqlBuilder.append(" ) \n");
        return this;
    }

    public SQLBuilder HAVING(String condition) {
        sqlBuilder.append(" HAVING ")
                .append(condition)
                .append("\n");
        return this;
    }


    public String build() {
        return sqlBuilder.toString();
    }

//    public static void main(String[] args) {
//        String sql_1 = new SQLBuilder()
//                .SELECT("name", "age")
//                .FROM("student")
//                .WHERE("age>10")
//                .build();
//        System.out.println(sql_1);
//        String findSql = PageSqlUtils.getRowsSQL(sql_1, 1, 10);
//        System.out.println(findSql);
//        String getNumberSQL = PageSqlUtils.getNumberSQL(sql_1);
//        System.out.println(getNumberSQL);
//
//        String city = "成都";
//        String sql_2 = new SQLBuilder()
//                .SELECT("s.name", "s.age")
//                .FROM("student s")
//                .LEFT_JOIN_ON("city c", "c.id=s.id")
//                .WHERE("s.age>10")
//                .IF_PRESENT_AND(city, "city.name LIKE :city")//如果把city改为null或者"" 这句话将不会添加
//                .build();
//        System.out.println(sql_2);
//
//
//        String sql_3 = new SQLBuilder()
//                .SELECT("s.name", "s.age")
//                .FROM("student s")
//                .LEFT_JOIN_ON("city c", "c.id=s.id")
//                .WHERE()
//                .AND("(age>10 OR age<5)")
//                .ORDER_BY("s.age")
//                .build();
//        System.out.println(sql_3);
//    }
}
