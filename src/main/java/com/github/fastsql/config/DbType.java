package com.github.fastsql.config;

/**
 * 数据库类型
 *
 * @author 陈佳志
 * 2017-08-17
 */
public enum DbType {
    MY_SQL("mysql"),
    POSTGRESQL("postgresql"),
    ORACLE("oracle");

    public String code;

    DbType(String code) {
        this.code = code;
    }


    public static DbType getByCode(String paramCode) {
        DbType[] values = DbType.values();
        for (DbType value : values) {
            if (value.code.equals(paramCode)) {
                return value;
            }
        }
        return null;
    }
}
