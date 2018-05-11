package top.fastsql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.util.StringUtils;
import top.fastsql.config.DataSourceType;
import top.fastsql.dto.BatchUpdateResult;
import top.fastsql.dto.ColumnMetaData;
import top.fastsql.dto.ResultPage;
import top.fastsql.dto.RowMap;
import top.fastsql.mapper.OraclePagingSingleColumnRowMapper;
import top.fastsql.util.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.function.Consumer;

/**
 * sql语句构建器和执行器
 *
 * @author 陈佳志
 */
@SuppressWarnings("unused")
public class SQL {
    private Logger logger = LoggerFactory.getLogger(SQL.class);

    private StringBuilder strBuilder = new StringBuilder();

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private boolean useClassicJdbcTemplate = false;


    private SqlParameterSource sqlParameterSource = new EmptySqlParameterSource();

    private Object[] varParams;

    private DataSourceType dataSourceType;


    public SQL() {

    }

    public SQL(JdbcTemplate jdbcTemplate, DataSourceType dataSourceType) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.dataSourceType = dataSourceType;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    /**
     * 追加任意字符串
     */
    public SQL append(String string) {
        strBuilder.append(string);
        return this;
    }

    /**
     * 追加空格
     */
    public SQL blank() {
        strBuilder.append(" ");
        return this;
    }

    /**
     * 追加空格和其他字符串
     */
    public SQL blank(String string) {
        strBuilder.append(" ").append(string);
        return this;
    }

    /**
     * 追加逗号
     */
    public SQL comma() {
        strBuilder.append(",");
        return this;
    }

    /**
     * 追加逗号和其他字符串
     */
    public SQL comma(String sql) {
        strBuilder.append(",").append(sql);
        return this;
    }

    /**
     * 使用sql字符串
     */
    public SQL useSql(String sql) {
        strBuilder.append(sql);
        return this;
    }


    /**
     * 生成左括号和右括号
     */
    public SQL $_$(SQL sql) {
        strBuilder.append(" (").append(sql.build()).append(")");
        return this;
    }

    /**
     * 生成左括号和右括号
     */
    public SQL $_$(String sqlStr) {
        strBuilder.append(" (").append(sqlStr).append(")");
        return this;
    }

    /**
     * 追加换行符
     */
    public SQL nl() {
        strBuilder.append("\n");
        return this;
    }


    public SQL SELECT() {
        strBuilder.append("SELECT ");
        return this;
    }

    public SQL SELECT(String... columns) {
        String columnsStr = String.join(",", columns);
        strBuilder.append("SELECT ").append(columnsStr);
        return this;
    }

    public SQL SELECT_DISTINCT(String... columns) {
        String columnsStr = String.join(",", columns);
        strBuilder.append("SELECT DISTINCT ").append(columnsStr);
        return this;
    }

    public SQL SELECT_count_FROM(String tableName) {
        return this.SELECT("count(*)").FROM(tableName);
    }

    public SQL SELECT_all_FROM(String tableName) {
        return this.SELECT("*").FROM(tableName);
    }

    @Deprecated
    public SQL append_SELECT(String... columns) {
        String columnsStr = String.join(",", columns);
        strBuilder.append(",").append(columnsStr);
        return this;
    }

    public SQL appendSELECT(String... columns) {
        String columnsStr = String.join(",", columns);
        strBuilder.append(",").append(columnsStr);
        return this;
    }

    public SQL AS(String value) {
        strBuilder.append(" AS ").append(value);
        return this;
    }


    public SQL DELETE_FROM(String table) {
        strBuilder.append("DELETE FROM ").append(table);
        return this;
    }


    public SQL INSERT_INTO(String table, String... columns) {
        if (columns.length == 0) {
            strBuilder.append("INSERT INTO ").append(table);
        } else {
            String columnsStr = String.join(",", columns);
            strBuilder.append("INSERT INTO ").append(table).append(" (").append(columnsStr).append(")");
        }

        return this;
    }

    public SQL INSERT_INTO(String table, List<String> columns) {
        String columnsStr = String.join(",", columns);
        strBuilder.append("INSERT INTO ").append(table).append(" (").append(columnsStr).append(")");
        return this;
    }

    public SQL VALUES(String... columnValues) {
        String columnsStr = String.join(",", columnValues);
        strBuilder.append(" VALUES ").append("(").append(columnsStr).append(")");
        return this;
    }

    public SQL VALUES(List<String> columnValues) {
        String columnsStr = String.join(",", columnValues);
        strBuilder.append(" VALUES ").append("(").append(columnsStr).append(")");
        return this;
    }


    /////////////////////update////////////////
    public SQL UPDATE(String table) {
        strBuilder.append("UPDATE ").append(table);
        return this;
    }

    public SQL SET(String... columnEqValue) {
        strBuilder.append(" SET ");
        for (int i = 0; i < columnEqValue.length; i++) {
            if (i != 0) {
                strBuilder.append(",");
            }
            strBuilder.append(columnEqValue[i]);
        }
        return this;
    }

    /////////////////////////////////////
    public SQL FROM(String table) {
        strBuilder.append(" FROM ").append(table);
        return this;
    }

    public SQL FROM_as(String table, String as) {
        strBuilder.append(" FROM ").append(table).append(" ").append(as);
        return this;
    }

    public SQL FROM(String table1, String table2) {
        strBuilder.append(" FROM ").append(table1).append(",").append(table2);
        return this;
    }

    public SQL FROM() {
        strBuilder.append(" FROM ");
        return this;
    }

    public SQL INNER_JOIN_ON(String table, String on) {
        strBuilder.append(" INNER JOIN ").append(table).append(" ON (").append(on).append(" )");
        return this;
    }

    public SQL FULL_OUTER_JOIN_ON(String table, String on) {
        strBuilder.append(" FULL OUTER JOIN ").append(table).append(" ON (").append(on).append(" )");
        return this;
    }

    public SQL LEFT_OUTER_JOIN_ON(String table, String on) {
        strBuilder.append(" LEFT OUTER JOIN ").append(table).append(" ON (").append(on).append(" )");
        return this;
    }

    public SQL LEFT_JOIN_ON(String table, String on) {
        strBuilder.append(" LEFT JOIN ").append(table).append(" ON (").append(on).append(" )");
        return this;
    }


    public SQL RIGHT_OUTER_JOIN_ON(String table, String on) {
        strBuilder.append(" RIGHT OUTER JOIN ").append(table).append(" ON (").append(on).append(")");
        return this;
    }

    public SQL RIGHT_JOIN_ON(String table, String on) {
        strBuilder.append(" RIGHT JOIN ").append(table).append(" ON (").append(on).append(")");
        return this;
    }

    public SQL WHERE() {
        strBuilder.append(" WHERE 1 = 1");
        return this;
    }

    public SQL WHERE(String condition) {
        strBuilder.append(" WHERE ").append(condition);
        return this;
    }


    public SQL ifPresentAND(Object object, String condition) {
        if (object == null || "".equals(object)) {
            return this;
        } else {
            strBuilder.append(" AND ").append(condition);
            return this;
        }
    }

    public SQL ifTrueAND(boolean b, String condition) {
        if (b) {
            strBuilder.append(" AND ").append(condition);
        }
        return this;
    }

    public SQL AND(String condition) {
        strBuilder.append(" AND ").append(condition);
        return this;
    }

    public SQL AND() {
        strBuilder.append(" AND");
        return this;
    }


    public SQL OR(String condition) {
        strBuilder.append(" OR ").append(condition);
        return this;
    }

    public SQL EXISTS() {
        strBuilder.append(" EXISTS ");
        return this;
    }

    public SQL EXISTS(String condition) {
        strBuilder.append(" EXISTS ").append(" ( ").append(condition).append(" ) ");
        return this;
    }

    public SQL NOT_EXISTS() {
        strBuilder.append(" NOT EXISTS ");
        return this;
    }

    public SQL NOT_EXISTS(String condition) {
        strBuilder.append(" NOT EXISTS ").append(" ( ").append(condition).append(" )");
        return this;
    }

    public SQL BETWEEN_AND(String from, String to) {
        strBuilder.append(" BETWEEN ").append(from).append(" AND ").append(to);
        return this;
    }

    public SQL ORDER_BY(String... condition) {
        strBuilder.append(" ORDER BY ").append(String.join(",", condition));
        return this;
    }

    public SQL GROUP_BY(String... condition) {
        strBuilder.append(" GROUP BY ").append(String.join(",", condition));
        return this;
    }

    public SQL ASC() {
        strBuilder.append(" ASC");
        return this;
    }

    public SQL DESC() {
        strBuilder.append(" DESC");
        return this;
    }

    //////////////////////////////////IN///////////////////////////////////////
    public SQL IN() {
        strBuilder.append(" IN ");
        return this;
    }

    public SQL NOT_IN() {
        strBuilder.append(" NOT IN ");
        return this;
    }

    public SQL IN(String sql) {
        strBuilder.append(" IN ").append(sql);
        return this;
    }

    public SQL NOT_IN(String sql) {
        strBuilder.append(" NOT IN ").append(sql);
        return this;
    }

    public SQL IN(Collection<?> collection) {
        strBuilder.append(" IN ").append(FastSqlUtils.getInClause(collection));
        return this;
    }

    public SQL IN(Object[] array) {
        strBuilder.append(" IN ").append(FastSqlUtils.getInClause(Arrays.asList(array)));
        return this;
    }

    public SQL NOT_IN(Collection<?> collection) {
        strBuilder.append(" NOT IN ").append(FastSqlUtils.getInClause(collection));
        return this;
    }

    public SQL NOT_IN(Object[] array) {
        strBuilder.append(" NOT IN ").append(FastSqlUtils.getInClause(Arrays.asList(array)));
        return this;
    }

    //-----------------  operator method--------------------------------

    /**
     * Generate equal operator and append the param
     */
    public SQL eq(String value) {
        strBuilder.append(" = ").append(value);
        return this;
    }

    /**
     * Generate equal operator
     */
    public SQL eq() {
        strBuilder.append(" = ");
        return this;
    }

    /**
     * Generate '>' operator and append the param
     */
    public SQL gt(String value) {
        strBuilder.append(" > ").append(value);
        return this;
    }

    /**
     * Generate '>' operator
     */
    public SQL gt() {
        strBuilder.append(" > ");
        return this;
    }

    /**
     * >= operator
     */
    public SQL gtEq(String value) {
        strBuilder.append(" >= ").append(value);
        return this;
    }

    /**
     * >= operator
     */
    public SQL gtEq() {
        strBuilder.append(" >= ");
        return this;
    }

    /**
     * <= operator
     */
    public SQL lt(String value) {
        strBuilder.append(" < ").append(value);
        return this;
    }

    public SQL lt() {
        strBuilder.append(" < ");
        return this;
    }

    /**
     * <=
     */
    public SQL ltEq(String value) {
        strBuilder.append(" <= ").append(value);
        return this;
    }

    /**
     *
     */
    public SQL ltEq() {
        strBuilder.append(" <= ");
        return this;
    }

    public SQL IS_NULL() {
        strBuilder.append(" IS NULL");
        return this;
    }

    public SQL IS_NOT_NULL() {
        strBuilder.append(" IS NOT NULL");
        return this;
    }

    public SQL nEq(String value) {
        strBuilder.append(" != ").append(value);
        return this;
    }

    public SQL nEq() {
        strBuilder.append(" != ");
        return this;
    }

    public SQL LIKE(String value) {
        strBuilder.append(" LIKE ").append(value);
        return this;
    }

    public SQL LIKE() {
        strBuilder.append(" LIKE ");
        return this;
    }

    public SQL NOT_LIKE(String value) {
        strBuilder.append(" LIKE ").append(value);
        return this;
    }

    public SQL NOT_LIKE() {
        strBuilder.append(" LIKE ");
        return this;
    }

    /**
     * 根据类型判断如何拼接SQL
     *
     * @param value 值
     * @see SQL#eq(String)
     */
    public SQL eqByType(Object value) {
        if (value == null) {
            strBuilder.append(" IS NULL");
        } else {
            //TODO 1.日期 还不支持oracle 使用 this.dataSourceType 判断
            strBuilder.append(" = ").append(getStringByType(value));
        }
        return this;
    }

    public SQL byType(Object value) {
        if (value == null) {
            strBuilder.append(" NULL");
        } else {
            //TODO 1.日期 还不支持oracle 使用 this.dataSourceType 判断
            strBuilder.append(getStringByType(value));
        }
        return this;
    }

    private String getStringByType(Object value) {
        if (value instanceof Number) {
            return value + "";
        } else if (value instanceof java.util.Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (value instanceof CharSequence) {
            return "'" + value + "'";
        } else {
            return "'" + value + "'";
        }
    }

    ///////////////////////////分页关键字///////////////////////////
    public SQL LIMIT(Integer offset, Integer rows) {
        strBuilder.append(" LIMIT ").append(offset).append(",").append(rows);
        return this;
    }

    public SQL LIMIT(Integer rows) {
        strBuilder.append(" LIMIT ").append(rows);
        return this;
    }

    public SQL OFFSET(Integer offset) {
        strBuilder.append(" OFFSET ").append(offset);
        return this;
    }

    public SQL UNION() {
        strBuilder.append("\nUNION\n");
        return this;
    }

    public SQL UNION_ALL() {
        strBuilder.append("\nUNION ALL\n");
        return this;
    }

    public SQL HAVING(String condition) {
        strBuilder.append(" HAVING ").append(condition);
        return this;
    }

    public SQL printSQL() {
        logger.info(strBuilder.toString());
        return this;
    }

    public String build() {
        return strBuilder.toString();
    }

    public SQL countThis() {
        this.strBuilder = new StringBuilder(PageUtils.getSmartCountSQL(strBuilder.toString()));
        return this;
    }

    public SQL pageThis(int pageNumber, int perPageSize) {
        this.strBuilder = new StringBuilder(PageUtils.getRowsSQL(strBuilder.toString(), pageNumber, perPageSize, this.dataSourceType));
        return this;
    }

    public SQL top(int number) {
        this.pageThis(1, number);
        return this;
    }

    public SQL top1() {
        this.top(1);
        return this;
    }

    /**
     * 2.16 is my birthday !
     */
    public SQL top216() {
        this.top(216);
        return this;
    }

    public SQL parameter(SqlParameterSource sqlParameterSource) {
        this.sqlParameterSource = sqlParameterSource;
        return this;
    }

    /**
     * 通过Map添加命名参数
     *
     * @param mapParameter Map参数
     */
    public SQL mapParameter(Map<String, Object> mapParameter) {
        this.sqlParameterSource = new MapSqlParameterSource(mapParameter);
        return this;
    }

    /**
     * 通过多个Map添加命名参数
     *
     * @param mapParameter 多个Map参数
     */
    public SQL mapParameter(Map<String, Object>... mapParameter) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        for (Map<String, Object> map : mapParameter) {
            mapSqlParameterSource.addValues(map);
        }
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }


    public SQL mapItemsParameter(String k1, Object v1) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(k1, v1);
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }

    public SQL mapItemsParameter(String k1, Object v1, String k2, Object v2) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(k1, v1);
        mapSqlParameterSource.addValue(k2, v2);
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }

    public SQL mapItemsParameter(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(k1, v1);
        mapSqlParameterSource.addValue(k2, v2);
        mapSqlParameterSource.addValue(k3, v3);
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }

    public SQL mapItemsParameter(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(k1, v1);
        mapSqlParameterSource.addValue(k2, v2);
        mapSqlParameterSource.addValue(k3, v3);
        mapSqlParameterSource.addValue(k4, v4);
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }

    public SQL mapItemsParameter(String k1, Object v1, String k2, Object v2, String k3, Object v3,
                                 String k4, Object v4, String k5, Object v5) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(k1, v1);
        mapSqlParameterSource.addValue(k2, v2);
        mapSqlParameterSource.addValue(k3, v3);
        mapSqlParameterSource.addValue(k4, v4);
        mapSqlParameterSource.addValue(k5, v5);
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }

    public SQL addMapParameterItem(String key, Object value) {
        if (this.sqlParameterSource instanceof EmptySqlParameterSource) {
            //新建一个MapSqlParameterSource
            this.sqlParameterSource = new MapSqlParameterSource(key, value);
        } else if (this.sqlParameterSource instanceof BeanPropertySqlParameterSource) {
            BeanPropertySqlParameterSource propertySqlParameterSource = (BeanPropertySqlParameterSource) this.sqlParameterSource;

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            //TODO propertySqlParameterSource.getReadablePropertyNames 多了class属性
            for (String name : propertySqlParameterSource.getReadablePropertyNames()) {
                mapSqlParameterSource.addValue(name, propertySqlParameterSource.getValue(name));
            }
            //add
            mapSqlParameterSource.addValue(key, value);
            this.sqlParameterSource = mapSqlParameterSource;
        } else if (this.sqlParameterSource instanceof MapSqlParameterSource) {
            ((MapSqlParameterSource) this.sqlParameterSource).addValue(key, value);
        } else {
            throw new RuntimeException("当前参数不支持addParameterMapItem");
        }
        return this;

    }

    public SQL appendMapParameterItem(String key, Object value) {
        return addMapParameterItem(key, value);
    }

    /**
     * 出现相同名称的参数时，map或覆盖bean中的参数
     *
     * @param bean bean
     * @param map  map
     */
    public SQL beanAndMapParameter(Object bean, Map<String, Object> map) {
        BeanPropertySqlParameterSource propertySqlParameterSource = new BeanPropertySqlParameterSource(bean);

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        for (String name : propertySqlParameterSource.getReadablePropertyNames()) {
            mapSqlParameterSource.addValue(name, propertySqlParameterSource.getValue(name));
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            mapSqlParameterSource.addValue(entry.getKey(), entry.getValue());
        }
        this.sqlParameterSource = mapSqlParameterSource;
        return this;
    }


    public SQL beanParameter(Object beanParam) {
        this.sqlParameterSource = new BeanPropertySqlParameterSource(beanParam);
        return this;
    }

    /**
     * 可变参数，替换Sql中使用?占位符
     *
     * @param vars 参数列表
     */
    public SQL varParameter(Object... vars) {
        this.useClassicJdbcTemplate = true;
        if (this.varParams == null) {
            this.varParams = vars;
        } else {
            List<Object> objectList = new ArrayList<>();
            Collections.addAll(objectList, this.varParams);
            Collections.addAll(objectList, vars);
            this.varParams = objectList.toArray();
        }
        return this;
    }


    public SQL useClassicTemplate(boolean b) {
        this.useClassicJdbcTemplate = b;
        return this;
    }

    /**
     * 查询单行结果封装为一个对象
     *
     * @param returnClassType Class&lt;T&gt;类型 T 可以为
     *                        String/Integer/Long/Short/BigDecimal/BigInteger/Float/Double/Boolean/Date或者任意POJO
     * @return 查询结果
     */
    public <T> T queryOne(Class<T> returnClassType) {
        checkNull();
        //jdbc 不支持 LocalDate LocalDateTime
        if (returnClassType.equals(LocalDate.class)) {
            return (T) queryLocalDate();
        }
        if (returnClassType.equals(LocalDateTime.class)) {
            return (T) queryLocalDateTime();
        }
        //
        RowMapper<T> rowMapper = getRowMapper(returnClassType);
        return useTemplateQueryOne(rowMapper);
    }

    /**
     * 分页并取得第一条记录
     *
     * @since 1.3.1
     */
    public <T> T queryFirst(Class<T> returnClassType) {
        this.pageThis(1, 1);
        return queryOne(returnClassType);
    }

    public Integer queryInteger() {
        return queryOne(Integer.class);
    }

    public Long queryLong() {
        return queryOne(Long.class);
    }

    public String queryString() {
        return queryOne(String.class);
    }

    public Date queryDate() {
        return queryOne(Date.class);
    }

    public LocalDate queryLocalDate() {
        return FastSqlUtils.toLocalDate(queryOne(Date.class));
    }

    public LocalDateTime queryLocalDateTime() {
        return FastSqlUtils.toLocalDateTime(queryOne(Date.class));
    }

    public <T> T queryOne(RowMapper<T> rowMapper) {
        checkNull();
        return useTemplateQueryOne(rowMapper);
    }

    /**
     * 使用  JdbcTemplate 查询一个
     */
    private <T> T useTemplateQueryOne(RowMapper<T> rowMapper) {
        try {
            String sql = this.build();
            if (useClassicJdbcTemplate) {
                return this.namedParameterJdbcTemplate.getJdbcOperations().queryForObject(sql, rowMapper, this.varParams);
            } else {
                return this.namedParameterJdbcTemplate.queryForObject(sql, this.sqlParameterSource, rowMapper);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询单行结果封装为Map
     *
     * @return Map
     */
    public Map<String, Object> queryMap() {
        checkNull();
        try {
            if (this.useClassicJdbcTemplate) {
                return this.namedParameterJdbcTemplate.getJdbcOperations().queryForMap(strBuilder.toString(), varParams);
            } else {
                return this.namedParameterJdbcTemplate.queryForMap(strBuilder.toString(), this.sqlParameterSource);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 查询单行结果封装为Map
     *
     * @return Map
     */
    public RowMap queryRowMap() {

        Map<String, Object> map = queryMap();
        if (map == null) {
            return null;
        }
        return new RowMap(map);
    }

    /**
     * 查询多行结果封装为一个对象列表
     *
     * @param returnClassType Class&lt;T&gt;类型 T 可以为String/Integer/Long/Short/BigDecimal/BigInteger/Float/Double/Boolean或者任意DTO
     */
    public <T> List<T> queryList(Class<T> returnClassType) {
        checkNull();
        RowMapper<T> rowMapper = getRowMapper(returnClassType);
        if (this.useClassicJdbcTemplate) {
            return this.namedParameterJdbcTemplate.getJdbcOperations().query(strBuilder.toString(), rowMapper, varParams);
        }

        return this.namedParameterJdbcTemplate.query(strBuilder.toString(), this.sqlParameterSource, rowMapper);
    }

    public List<String> queryStringList() {
        checkNull();
        RowMapper<String> rowMapper = new SingleColumnRowMapper<>(String.class);
        if (this.useClassicJdbcTemplate) {
            return this.namedParameterJdbcTemplate.getJdbcOperations().query(strBuilder.toString(), rowMapper, varParams);
        }

        return this.namedParameterJdbcTemplate.query(strBuilder.toString(), this.sqlParameterSource, rowMapper);
    }

    public List<Integer> queryIntegerList() {
        checkNull();
        RowMapper<Integer> rowMapper = new SingleColumnRowMapper<>(Integer.class);
        if (this.useClassicJdbcTemplate) {

            return this.namedParameterJdbcTemplate.getJdbcOperations().query(strBuilder.toString(), rowMapper, varParams);
        }

        return this.namedParameterJdbcTemplate.query(strBuilder.toString(), this.sqlParameterSource, rowMapper);
    }

    public <T> List<T> queryList(RowMapper<T> rowMapper) {
        checkNull();
        if (this.useClassicJdbcTemplate) {

            return this.namedParameterJdbcTemplate.getJdbcOperations().query(strBuilder.toString(), rowMapper, varParams);
        }

        return this.namedParameterJdbcTemplate.query(strBuilder.toString(), this.sqlParameterSource, rowMapper);
    }

    /**
     * 查出一个map ： key为指定的列，value 为传入类型
     */
    public <T> Map<String, T> queryColumnKeyMap(String column, Class<T> returnClassType) {
        List<T> list = queryList(returnClassType);
        Map<String, T> map = new HashMap<>();
        for (T row : list) {
            map.put(
                    (String) EntityRefelectUtils.getFieldValue(row, StringExtUtils.underlineToCamel(column)),
                    row
            );
        }
        return map;
    }

    /**
     * 查询多行结果封装为Map列表
     *
     * @return Map数组
     */
    public List<Map<String, Object>> queryMapList() {
        checkNull();
        if (this.useClassicJdbcTemplate) {
            return this.namedParameterJdbcTemplate.getJdbcOperations().queryForList(strBuilder.toString(), varParams);
        }
        return this.namedParameterJdbcTemplate.queryForList(strBuilder.toString(), this.sqlParameterSource);
    }


    public List<RowMap> queryRowMapList() {
        List<Map<String, Object>> mapList = queryMapList();

        List<RowMap> valueMapList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {

            valueMapList.add(new RowMap(map));
        }
        return valueMapList;
    }

    /**
     * 查询多行结果封装为Object[]列表
     *
     * @return Object[]列表
     */
    public List<Object[]> queryArrayList() {
        checkNull();
        if (this.useClassicJdbcTemplate) {
            return this.namedParameterJdbcTemplate.getJdbcOperations().query(strBuilder.toString(), varParams, (rs, rowNum) -> {
                int columnCount = rs.getMetaData().getColumnCount();
                Object[] objects = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    objects[i - 1] = rs.getObject(i);
                }
                return objects;
            });
        } else {
            return this.namedParameterJdbcTemplate.query(strBuilder.toString(), this.sqlParameterSource, (rs, rowNum) -> {
                int columnCount = rs.getMetaData().getColumnCount();
                Object[] objects = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    objects[i - 1] = rs.getObject(i);
                }
                return objects;
            });
        }
    }

    public List<Map<String, Object>> queryMapListAndPrint() {
        List<Map<String, Object>> mapList = queryMapList();
        StringBuilder out = new StringBuilder("result:\n[\n");
        for (Map<String, Object> map : mapList) {
            out.append("  ").append(map).append("\n");
        }
        out.append("]");
        logger.info(out.toString());
        return mapList;
    }

    public List<Object[]> queryArrayListAndPrint() {
        List<Object[]> objects = queryArrayList();
        StringBuilder out = new StringBuilder("result:\n[\n");
        for (Object[] object : objects) {
            out.append("  ").append(Arrays.toString(object)).append("\n");
        }
        out.append("]");
        logger.info(out.toString());
        return objects;
    }

    /**
     * 查询结果页
     * page <=0 查询数量
     * perPage <=0 仅查询条数
     *
     * @param page            第几页 从1开始
     * @param perPage         每页几条 最小为1
     * @param returnClassType 返回的结果类型
     * @return 返回的结果页
     */
    public <T> ResultPage<T> queryPage(int page, int perPage, Class<T> returnClassType) {
        checkNull();
        RowMapper<T> rowMapper = getRowMapper(returnClassType);

        if (useClassicJdbcTemplate) {

            return new PageTemplate(namedParameterJdbcTemplate)
                    .queryPage(strBuilder.toString(), page, perPage, varParams, rowMapper, this.dataSourceType);
        } else {
            return new PageTemplate(namedParameterJdbcTemplate)
                    .queryPage(strBuilder.toString(), page, perPage, sqlParameterSource, rowMapper, this.dataSourceType);
        }
    }

    /**
     * page <=0 查询数量
     * perPage <=0 仅查询条数
     *
     * @param page      第几页 从1开始
     * @param perPage   每页几条 最小为1
     * @param rowMapper 结果类型
     * @return 返回的结果页
     */
    public <T> ResultPage<T> queryPage(int page, int perPage, RowMapper<T> rowMapper) {
        checkNull();

        if (useClassicJdbcTemplate) {
            return new PageTemplate(namedParameterJdbcTemplate)
                    .queryPage(strBuilder.toString(), page, perPage, varParams, rowMapper, this.dataSourceType);
        } else {
            return new PageTemplate(namedParameterJdbcTemplate)
                    .queryPage(strBuilder.toString(), page, perPage, sqlParameterSource, rowMapper, this.dataSourceType);
        }
    }


    /**
     * 查询结果内存分页
     *
     * @param page            第几页 从1开始
     * @param perPage         每页几条 最小为1
     * @param returnClassType 返回的结果类型
     * @return 返回的结果页
     */
    public <T> ResultPage<T> queryMemoryPage(int page, int perPage, Class<T> returnClassType) {
        List<T> dataList = queryList(returnClassType);
        int total = dataList.size();
        List<T> list = FastSqlUtils.memoryPage(dataList, page, perPage);
        return new ResultPage<>(list, total);
    }

    /**
     * 执行INSERT/UPDATE/DELETE语句
     *
     * @return 返回更新的行数
     */
    public int update() {
        checkNull();
        int count;
        String sql = strBuilder.toString();
        if (useClassicJdbcTemplate) {
            count = this.namedParameterJdbcTemplate.getJdbcOperations().update(sql, varParams);
        } else {
            count = this.namedParameterJdbcTemplate.update(sql, this.sqlParameterSource);
        }
        if (count < 1) {
            logger.warn("update更新成功数量为" + count);
        }
        return count;
    }


    public BatchUpdateResult batchUpdateByMapParams(List<Map<String, Object>> mapParamList) {
        checkNull();
        String sql = strBuilder.toString();
        SqlParameterSource[] batchArgs = new SqlParameterSource[mapParamList.size()];
        for (int i = 0; i < mapParamList.size(); i++) {
            batchArgs[i] = new MapSqlParameterSource(mapParamList.get(i));
        }
        return new BatchUpdateResult(this.namedParameterJdbcTemplate.batchUpdate(sql, batchArgs));
    }

    public BatchUpdateResult batchUpdateWithSql(String... sql) {
        checkNull();
        return new BatchUpdateResult(this.namedParameterJdbcTemplate.getJdbcOperations().batchUpdate(sql));
    }

    public BatchUpdateResult batchUpdateWithSql(List<String> sqls) {
        checkNull();
        String[] sqlArray = new String[sqls.size()];
        int i = 0;
        for (String sql : sqls) {
            sqlArray[i] = sql;
            i++;
        }
        return new BatchUpdateResult(this.namedParameterJdbcTemplate.getJdbcOperations().batchUpdate(sqlArray));
    }

    public BatchUpdateResult batchUpdateByArrays(List<Object[]> objects) {
        checkNull();
        String sql = strBuilder.toString();
        return new BatchUpdateResult(this.namedParameterJdbcTemplate.getJdbcOperations().batchUpdate(sql, objects));
    }

    public List<String> getTableNames() {
        checkNull();
        Connection connection;
        try {
            connection = this.getDataSource().getConnection();
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "%%", new String[]{"TABLE"});
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tables.add(tableName);
            }
            resultSet.close();
            connection.close();
            return tables;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getTableNames(String catalog, String schemaPattern,
                                      String tableNamePattern) {
        checkNull();
        Connection connection;
        try {
            connection = this.getDataSource().getConnection();
            ResultSet resultSet = connection.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, new String[]{"TABLE"});
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tables.add(tableName);
            }
            resultSet.close();
            connection.close();
            return tables;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getColumnNames(String tableName) {
        checkNull();
        List<String> columns = new ArrayList<>();
        Connection connection;
        try {
            connection = this.getDataSource().getConnection();
            ResultSet resultSet = connection.getMetaData().getColumns(null, "%", tableName, "%");

            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                columns.add(columnName);
            }
            resultSet.close();
            connection.close();
            return columns;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 参考 http://www.cnblogs.com/lbangel/p/3487796.html
     *
     * @param tableName 表名称
     */
    public List<ColumnMetaData> getColumnMetaDataList(String tableName) {
        checkNull();
        List<ColumnMetaData> columnMetaDataList = new ArrayList<>();
        Connection connection;
        try {
            connection = this.getDataSource().getConnection();
            ResultSet resultSet = connection.getMetaData().getColumns(null, "%", tableName, "%");

            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String typeName = resultSet.getString("TYPE_NAME");
                int type = resultSet.getInt("DATA_TYPE");
                String def = resultSet.getString("COLUMN_DEF");

                ColumnMetaData columnMetaData = new ColumnMetaData();
                columnMetaData.setColumnTypeName(typeName);
                columnMetaData.setColumnName(columnName);
                columnMetaData.setColumnType(type);
                columnMetaData.setDefaultValue(def);
                columnMetaDataList.add(columnMetaData);
            }

            resultSet.close();
            connection.close();
            return columnMetaDataList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SQL ifPresent(Object object, Consumer<SQL> sQLConsumer) {
        return ifTrue(!StringUtils.isEmpty(object), sQLConsumer);
    }

    public SQL ifNotEmpty(Collection<?> collection, Consumer<SQL> sQLConsumer) {
        return ifTrue(collection.size() > 0, sQLConsumer);
    }

    public SQL ifTrue(boolean bool, Consumer<SQL> sQLConsumer) {
        if (bool) {
            sQLConsumer.accept(this);
        }
        return this;
    }

    public DataSource getDataSource() {
        checkNull();
        return ((JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations()).getDataSource();
    }

    //////////////////////////////////////private////////////////////////
    private void checkNull() {
        if (this.namedParameterJdbcTemplate == null) {
            throw new RuntimeException("namedParameterJdbcTemplate or database is not set.");
        }
    }


    private <T> RowMapper<T> getRowMapper(Class<T> returnClassType) {
        RowMapper<T> rowMapper;

        List<Class<?>> classArrayList =
                Arrays.asList(String.class,
                        Integer.class, int.class, Long.class, long.class,
                        Short.class, short.class,
                        BigDecimal.class,
                        BigInteger.class,
                        Float.class, float.class, Double.class, double.class,
                        Boolean.class, boolean.class,
                        java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
                        byte[].class, Blob.class, Clob.class);

        if (classArrayList.contains(returnClassType)) {
            if (this.dataSourceType.equals(DataSourceType.ORACLE)) {
                return new OraclePagingSingleColumnRowMapper<>(returnClassType);
            }
            return new SingleColumnRowMapper<>(returnClassType);
        } else if (returnClassType.equals(Map.class)) {
            return (RowMapper<T>) new ColumnMapRowMapper();
        } else {
            return new BeanPropertyRowMapper<>(returnClassType);
        }
    }

    @Override
    public String toString() {
        return this.build();
    }
}
