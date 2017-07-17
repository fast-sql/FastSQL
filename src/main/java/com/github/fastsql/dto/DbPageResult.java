package com.github.fastsql.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiazhi
 * @since 2017/4/4
 */
public class DbPageResult<T> {

    private List<T> rows = new ArrayList<>();

    private long total;


    public DbPageResult(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
