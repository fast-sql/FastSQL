package com.github.fastsql.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 陈佳志
 */
@ConfigurationProperties(
        ignoreUnknownFields = false,
        prefix = "fastsql")
public class FastSqlProperties {

    private String dbType = "postgresql";



    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }


}
