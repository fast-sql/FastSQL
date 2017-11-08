package org.fastsql.dto;

/**
 * 表元数据
 *
 * @author 陈佳志
 * 2017-10-18
 */
public class ColumnMetaData {

    private String columnName;
    private int columnType;
    private String columnTypeName;
    private String defaultValue;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "ColumnMetaData{" +
                "columnName='" + columnName + '\'' +
                ", columnType=" + columnType +
                ", columnTypeName='" + columnTypeName + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
