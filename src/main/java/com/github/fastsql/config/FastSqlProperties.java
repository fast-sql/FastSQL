package com.github.fastsql.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jiazhi
 * @since 2017/5/28
 */
@ConfigurationProperties(
        ignoreUnknownFields = false,
        prefix = "fastsql")
public class FastSqlProperties {

    private String dbType = "postgresql";

    private Boolean showSql = true;


    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Boolean getShowSql() {
        return showSql;
    }

    public void setShowSql(Boolean showSql) {
        this.showSql = showSql;
    }
}
