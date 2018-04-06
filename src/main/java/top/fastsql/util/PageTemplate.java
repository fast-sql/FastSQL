package top.fastsql.util;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import top.fastsql.config.DataSourceType;
import top.fastsql.dto.ResultPage;

import java.util.List;

/**
 * @author 陈佳志
 * 2017-08-15
 */
public class PageTemplate {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PageTemplate(NamedParameterJdbcTemplate template) {
        this.namedParameterJdbcTemplate = template;
    }


    public <T> ResultPage<T> queryPage(String sql, int page, int perPage, SqlParameterSource paramSource, RowMapper<T> rowMapper, DataSourceType dataSourceType) {
        String rowsSQL = PageUtils.getRowsSQL(sql, page, perPage, dataSourceType);
        List<T> list = namedParameterJdbcTemplate.query(
                rowsSQL,
                paramSource,
                rowMapper);

        //查询数量
        String numberSQL = PageUtils.getSmartCountSQL(sql);
        Integer number = namedParameterJdbcTemplate.queryForObject(
                numberSQL,
                paramSource,
                Integer.class);
        return new ResultPage<T>(list, number);
    }


    public <T> ResultPage<T> queryPage(String sql, int page, int perPage, Object[] objects, RowMapper<T> rowMapper, DataSourceType dataSourceType) {
        String rowsSQL = PageUtils.getRowsSQL(sql, page, perPage, dataSourceType);
        List<T> list = namedParameterJdbcTemplate.getJdbcOperations().query(
                rowsSQL,
                objects,
                rowMapper);

        //查询数量
        String numberSQL = PageUtils.getSmartCountSQL(sql);
        Integer number = namedParameterJdbcTemplate.getJdbcOperations().queryForObject(
                numberSQL,
                objects,
                Integer.class);
        return new ResultPage<T>(list, number);
    }
}
