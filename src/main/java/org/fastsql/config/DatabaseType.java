package org.fastsql.config;

/**
 * 数据库类型
 *
 * @author 陈佳志
 * 2017-08-17
 */
public enum DatabaseType {
    MY_SQL("mysql"),
    POSTGRESQL("postgresql"),
    ORACLE("oracle");

    public String code;

    DatabaseType(String code) {
        this.code = code;
    }


    public static DatabaseType getByCode(String paramCode) {
        DatabaseType[] values = DatabaseType.values();
        for (DatabaseType value : values) {
            if (value.code.equals(paramCode)) {
                return value;
            }
        }
        return null;
    }
}
