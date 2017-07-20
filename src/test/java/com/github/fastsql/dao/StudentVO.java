package com.github.fastsql.dao;

/**
 * @author 陈佳志
 *         2017-07-20
 */
public class StudentVO extends Student {
    private String cityName;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
