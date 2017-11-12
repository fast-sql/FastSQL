package top.fastsql.config;

/**
 * 数据库类型
 *
 * @author 陈佳志
 * 2017-08-17
 */
public enum DataSourceType {
    MY_SQL("mysql"),
    POSTGRESQL("postgresql"),
    ORACLE("oracle");

    public String code;

    DataSourceType(String code) {
        this.code = code;
    }


    public static DataSourceType getByCode(String paramCode) {
        DataSourceType[] values = DataSourceType.values();
        for (DataSourceType value : values) {
            if (value.code.equals(paramCode)) {
                return value;
            }
        }
        return null;
    }
}
