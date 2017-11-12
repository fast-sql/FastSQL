package top.fastsql;

import org.springframework.jdbc.core.JdbcTemplate;
import top.fastsql.config.DataSourceType;

import javax.sql.DataSource;

/**
 * SQL类的工厂类
 */
public class SQLFactory {
    private DataSource dataSource;

    private DataSourceType dataSourceType = DataSourceType.POSTGRESQL;

    private boolean ignoreWarnings = true;

    private int fetchSize = -1;

    private int maxRows = -1;

    private int queryTimeout = -1;

    private boolean skipResultsProcessing = false;

    private boolean skipUndeclaredResults = false;

    private boolean resultsMapCaseInsensitive = false;

    private boolean logSQLWhenBuild = false;

    public SQL createSQL() {
//        if (dataSource == null) {
//            throw new FastSQLException("SQLFactory的dataSource不能为null");
//        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setIgnoreWarnings(ignoreWarnings);
        jdbcTemplate.setFetchSize(fetchSize);
        jdbcTemplate.setMaxRows(maxRows);
        jdbcTemplate.setQueryTimeout(queryTimeout);
        jdbcTemplate.setSkipResultsProcessing(skipResultsProcessing);
        jdbcTemplate.setSkipUndeclaredResults(skipUndeclaredResults);
        jdbcTemplate.setResultsMapCaseInsensitive(resultsMapCaseInsensitive);
        return new SQL(jdbcTemplate, dataSourceType, logSQLWhenBuild);
    }


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public boolean isIgnoreWarnings() {
        return ignoreWarnings;
    }

    public void setIgnoreWarnings(boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public boolean isSkipResultsProcessing() {
        return skipResultsProcessing;
    }

    public void setSkipResultsProcessing(boolean skipResultsProcessing) {
        this.skipResultsProcessing = skipResultsProcessing;
    }

    public boolean isSkipUndeclaredResults() {
        return skipUndeclaredResults;
    }

    public void setSkipUndeclaredResults(boolean skipUndeclaredResults) {
        this.skipUndeclaredResults = skipUndeclaredResults;
    }

    public boolean isResultsMapCaseInsensitive() {
        return resultsMapCaseInsensitive;
    }

    public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
        this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
    }

    public boolean isLogSQLWhenBuild() {
        return logSQLWhenBuild;
    }

    public void setLogSQLWhenBuild(boolean logSQLWhenBuild) {
        this.logSQLWhenBuild = logSQLWhenBuild;
    }
}
