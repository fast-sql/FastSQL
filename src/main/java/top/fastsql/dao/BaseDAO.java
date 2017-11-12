package top.fastsql.dao;

import top.fastsql.SQL;
import top.fastsql.SQLFactory;
import top.fastsql.config.DataSourceType;
import top.fastsql.dto.ResultPage;
import top.fastsql.util.EntityRefelectUtils;
import top.fastsql.util.PageUtils;
import top.fastsql.util.StringExtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static top.fastsql.util.StringExtUtils.camelToUnderline;

/**
 * 基础DAO 提供CRUD等操作
 *
 * @author 陈佳志
 */
@SuppressWarnings({"unchecked", "StringConcatenationInsideStringBufferAppend", "SqlNoDataSourceInspection",
        "ConstantConditions", "WeakerAccess", "Duplicates"})
public abstract class BaseDAO<E, ID> {

    protected Class<E> entityClass;
    protected Class<ID> idClass;

    protected Logger log;//TODO 重写

    protected String className;
    protected String tableName;
    /**
     * 元数据
     */
    protected Field idField;
    protected String idColumnName;

    protected List<Field> fieldsWithoutId = new ArrayList<>();
    protected List<String> columnNamesWithoutId = new ArrayList<>();

    protected List<Field> fields = new ArrayList<>();
    protected List<String> columnNames = new ArrayList<>();

    /**
     * save/update/delete 拦截器  配置
     */
    protected boolean useBeforeInsert = false;
    protected boolean useAfterInsert = true;//
    protected boolean useBeforeUpdate = false;
    protected boolean useAfterUpdate = true;//
    protected boolean useBeforeDelete = false;
    protected boolean useAfterDelete = true;//

    protected DataSourceType dataSourceType;

    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected SQLFactory sqlFactory;

    //TODO 完全使用SQL代替NamedParameterJdbcTemplate
    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public BaseDAO() {
        initMetaData();
    }

    /**
     * 初始化DAO元数据
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void initMetaData() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            this.entityClass = (Class<E>) ((ParameterizedType) type).getActualTypeArguments()[0];
            this.idClass = (Class<ID>) ((ParameterizedType) type).getActualTypeArguments()[1];
        } else {
            this.entityClass = null;
        }

        //日志器
        this.log = LoggerFactory.getLogger(entityClass);

        this.className = entityClass.getSimpleName();
        this.tableName = EntityRefelectUtils.getTableNameFromEntityClass(this.entityClass);

        //TODO 现在反射了两次 需要重写为一次
        //没有主键
        this.fieldsWithoutId = EntityRefelectUtils.getAllFieldWithoutIdByClass(entityClass);
        /*
        for (Field field : this.fieldsWithoutId) {
            this.columnNamesWithoutId.add(camelToUnderline(field.getName()));
        }
        */
        this.fieldsWithoutId.forEach(field -> this.columnNamesWithoutId.add(camelToUnderline(field.getName())));
        //主键
        this.idField = EntityRefelectUtils.getIdField(entityClass); //TODO 2
        this.idColumnName = camelToUnderline(idField.getName());

        //所有
        this.fields.addAll(this.fieldsWithoutId);
        this.fields.add(0, this.idField);
        this.columnNames.addAll(this.columnNamesWithoutId);
        this.columnNames.add(0, camelToUnderline(this.idField.getName()));

        //使用默认配置
        this.dataSourceType = DataSourceType.POSTGRESQL;

        //sql工厂
        this.sqlFactory = new SQLFactory();
    }


    /////////////////////////////////////////////////保存方法////////////////////////////////////////

    /**
     * 插入对象中非null的值到数据库
     *
     * @param entity 实体类对象
     * @return 插入成功的数量
     */
    public int insertSelective(E entity) {
        //SQL语句部分字符串构建器
        final StringBuilder nameBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        /*
        for (Field field : fields) {
            if (EntityRefelectUtils.getFieldValue(entity, field) != null) {
                nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
                valueBuilder.append(",:").append(field.getName());
            }
        }
        String insertSql = "INSERT INTO " + tableName + "(" + nameBuilder.toString().replaceFirst(",", "") + ") " +
        "VALUES(" + valueBuilder.toString().replaceFirst(",", "") + ")";
        */
        //遍历
        fields.stream()
                .filter(field -> EntityRefelectUtils.getFieldValue(entity, field) != null)
                .forEach(field -> {
                    nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
                    valueBuilder.append(",:").append(field.getName());
                });
        //构建SQL实例
        final SQL sql = sqlFactory.createSQL()
                .INSERT_INTO(tableName, nameBuilder.deleteCharAt(0).toString())
                .VALUES(valueBuilder.delete(0, 1).toString())
                .beanParameter(entity);
        //若useBeforeInsert设置为true，则执行拦截方法
        if (useBeforeInsert) {
            beforeInsert(entity);
        }
        //执行sql语句并获取修改行数
        final int count = sql.update();
        //若useAfterInsert设置为true，则执行拦截方法
        if (useAfterInsert) {
            afterInsert(entity, count);
        }
        //返回修改行数
        return count;
    }


    /**
     * 插入对象中的值到数据库，null值在数据库中会设置为NULL
     */
    public int insert(E entity) {
        final StringBuilder nameBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        fields.forEach(field -> {
            nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
            valueBuilder.append(",:").append(field.getName());
        });
        /*
        for (Field field : fields) {
            nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
            valueBuilder.append(",:").append(field.getName());
        }
        */
        final SQL sql = sqlFactory.createSQL()
                .INSERT_INTO(tableName, nameBuilder.deleteCharAt(0).toString())
                .VALUES(valueBuilder.deleteCharAt(0).toString())
                .beanParameter(entity);
        /*
        String sql = "INSERT INTO " + tableName + "(" + nameBuilder.toString().replaceFirst(",", "") + ") " +
                "VALUES(" + valueBuilder.toString().replaceFirst(",", "") + ")";
        int count = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
        */
        if (useBeforeInsert) {
            beforeInsert(entity);
        }
        final int count = sql.update();
        if (useAfterInsert) {
            afterInsert(entity, count);
        }
        return count;
    }

    /////////////////////////////修改 /////////////////////////////////////////////

    /**
     * 全更新 null值在 数据库中设置为null
     */
    public int update(E entity) {
        //TODO
        final ID id = (ID) EntityRefelectUtils.getFieldValue(entity, idField);
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("修改时对象id不能为空");
        }
        final StringBuilder sqlBuilder = new StringBuilder();
        fieldsWithoutId
                .forEach(field ->
                        sqlBuilder.append("," + StringExtUtils.camelToUnderline(field.getName()) +
                                "=:" +
                                field.getName()));
        /*
        for (Field field : fieldsWithoutId) {
            sqlBuilder.append("," + StringExtUtils.camelToUnderline(field.getName()) + "=:" + field.getName());
        }
        String setValueSql = sqlBuilder.deleteCharAt(0).toString();
        String sql = "UPDATE " + tableName + " SET " + setValueSql + " WHERE id=:id";//set sql
        */
        final SQL sql = sqlFactory.createSQL()
                .UPDATE(tableName)
                .SET(sqlBuilder.deleteCharAt(0).toString())
                .WHERE(idColumnName + "=:" + idColumnName)
                .beanParameter(entity);
        /*
        if (useAfterUpdate) {
            beforeUpdate(entity);
        }
        int count = namedParameterJdbcTemplate.update(
                sql, new BeanPropertySqlParameterSource(entity)
        );
        */
        if (useBeforeUpdate) {
            beforeUpdate(entity);
        }
        final int count = sql.update();
        if (useAfterUpdate) {
            afterUpdate(entity, count);
        }
        return count;
    }

    public int insertOrUpdate(E entity) {
        final ID id = (ID) EntityRefelectUtils.getFieldValue(entity, idField);
        if (StringUtils.isEmpty(id)) {
            //插入
            return insert(entity);
        } else {
            //更新
            final E row = selectOneById(id);
            if (row == null) {
                return insert(entity);
            } else {
                return update(entity);
            }
        }
    }


    /**
     * 仅更新非null， null值 不更新
     */
    public int updateSelective(E entity) {
        final ID id = (ID) EntityRefelectUtils.getFieldValue(entity, idField);
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("修改时对象id不能为空");
        }
        final StringBuilder sqlBuilder = new StringBuilder();
        fieldsWithoutId.forEach(field ->
                sqlBuilder.append("," + StringExtUtils.camelToUnderline(field.getName()) +
                        "=:" +
                        field.getName()));
        /*
        for (Field field : fieldsWithoutId) {
            if (EntityRefelectUtils.getFieldValue(entity, field) != null) {
                sqlBuilder.append("," + StringExtUtils.camelToUnderline(field.getName()) + "=:" + field.getName());
            }
        }
        String setValueSql = sqlBuilder.toString().replaceFirst(",", "");
        String sql = "UPDATE " + tableName + " SET " + setValueSql + " WHERE id=:id";//set sql
        */
        final SQL sql = sqlFactory.createSQL()
                .UPDATE(tableName)
                .SET(sqlBuilder.deleteCharAt(0).toString())
                .WHERE(idColumnName + "=:" + idColumnName)
                .beanParameter(entity);
        /*
        int count = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
        */
        if (useBeforeUpdate) {
            beforeUpdate(entity);
        }
        final int count = sql.update();
        if (useAfterUpdate) {
            afterUpdate(entity, count);
        }
        return count;
    }

    public int updateColumns(E entity, String... columns) {
        final ID id = (ID) EntityRefelectUtils.getFieldValue(entity, idField);
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("修改时对象id不能为空");
        }
        final StringBuilder sqlBuilder = new StringBuilder();
        for (String column : columns) {
            sqlBuilder.append("," + column + "=:" + EntityRefelectUtils.underlineToCamelFirstLower(column));
        }
        /*
        String setValueSql = sqlBuilder.toString().replaceFirst(",", "");
        String sql = "UPDATE " + tableName + " SET " + setValueSql + " WHERE id=:id";//set sql
        int count = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(entity));
        */
        final SQL sql = sqlFactory.createSQL()
                .UPDATE(tableName)
                .SET(sqlBuilder.deleteCharAt(0).toString())
                .WHERE(idColumnName + "=:" + idColumnName)
                .beanParameter(entity);
        if (useBeforeUpdate) {
            beforeUpdate(entity);
        }
        final int count = sql.update();
        if (useAfterUpdate) {
            afterUpdate(entity, count);
        }
        return count;
    }


    /////////////////////////////////////////////////删除方法////////////////////////////////////////

    /**
     * 根据id删除数据
     */
    public int deleteOneById(ID id) {
        if (useBeforeDelete) {
            beforeDelete(id);
        }
        //String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";
        final SQL sql = sqlFactory.createSQL()
                .DELETE_FROM(tableName)
                .WHERE(idColumnName + "=:" + idColumnName)
                .mapItemsParameter("id", id);
        /*
        int count = namedParameterJdbcTemplate.getJdbcOperations().update(sql, id);
        */
        final int count = sql.update();
        if (useAfterDelete) {
            afterDelete(id, count);
        }
        return count;
    }

    /**
     * 删除所有数据
     */
    public int deleteAll() {
        //TODO 此方法可考虑不使用SQL类重写
        return namedParameterJdbcTemplate.getJdbcOperations().update("DELETE FROM " + tableName);
    }

    /**
     * 根据条件删除
     */
    public int deleteWhere(String sqlCondition, Object... values) {
        String sql = "DELETE FROM " + tableName + " WHERE " + sqlCondition;
        return namedParameterJdbcTemplate.getJdbcOperations().update(sql, values);
    }

    /**
     * 根据id列表批量删除数据
     */
    public int[] deleteInBatch(List<ID> ids) {
        final String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + "=:" + idColumnName;
        final MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            parameterSources[i] = new MapSqlParameterSource("id", ids.get(i));
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }


    //////////////////////////////find one/////////////////////////////////////

    /**
     * 通过id查找
     */
    public E selectOneById(ID id) {
        E returnObject;
        try {
            /*
            returnObject = namedParameterJdbcTemplate.getJdbcOperations().queryForObject(
                    "SELECT * FROM " + tableName + " WHERE " + idColumnName + "=?",
                    new BeanPropertyRowMapper<E>(entityClass),
                    id
            );
            */
            returnObject = sqlFactory.createSQL()
                    .SELECT("*")
                    .FROM(tableName)
                    .WHERE(idColumnName + "=:" + idColumnName)
                    .mapItemsParameter(idColumnName, id)
                    .queryOne(entityClass);
        } catch (EmptyResultDataAccessException e) {
            returnObject = null;
        }
        return returnObject;
    }


    private E selectOneWhere(String sqlCondition, Object param1) {
        return selectOneWhere(sqlCondition, new Object[]{param1});
    }

    private E selectOneWhere(String sqlCondition, Object param1, Object param2) {
        return selectOneWhere(sqlCondition, new Object[]{param1, param2});
    }

    private E selectOneWhere(String sqlCondition, Object param1, Object param2, Object param3) {
        return selectOneWhere(sqlCondition, new Object[]{param1, param2, param3});
    }

    /**
     * 通过where条件查找一条记录
     * 查找姓名为1年龄大于23的记录  selectOneWhere("name=? and age>?", "wang",23)
     *
     * @param sqlCondition name=:1 and age=:2
     * @param values       "wang",23
     */
    private E selectOneWhere(String sqlCondition, Object[] values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        List<E> dateList = namedParameterJdbcTemplate.getJdbcOperations().query(
                sql, values, new BeanPropertyRowMapper<>(entityClass)
        );

        /*
        TODO 不知如何用SQL重写
        sqlFactory.createSQL()
                .SELECT("*")
                .FROM(tableName)
                .WHERE(sqlCondition)
        */
        if (dateList.isEmpty()) {
//            log.warn(tableName + "#findOneWhere()返回的数据为null");
            return null;
        } else if (dateList.size() == 1) {
            return dateList.get(0);
        } else {
            log.error(tableName + "#findOneWhere()返回多条数据");
            throw new RuntimeException(tableName + "#findOneWhere()返回多条数据");
        }
    }


    public E selectOneWhere(String sqlCondition, SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        List<E> dateList = namedParameterJdbcTemplate.query(
                sql, parameterSource, new BeanPropertyRowMapper<>(entityClass)
        );
        if (dateList.size() == 0) {
            return null;
        } else if (dateList.size() == 1) {
            return dateList.get(0);
        } else {
            log.error(tableName + "#findOneWhere()返回多条数据");
            throw new RuntimeException(tableName + "#findOneWhere()返回多条数据");
        }
    }

    //////////////////////////////find list/////////////////////////////////////

    public List<E> selectAll() {
        /*
        String sql = "SELECT * FROM " + tableName;
        return namedParameterJdbcTemplate.query(sql, new HashMap<String, Object>(), new BeanPropertyRowMapper<E>(entityClass));
        */
        return sqlFactory.createSQL()
                .SELECT("*")
                .FROM(tableName)
                .queryList(entityClass);
    }

    public List<E> selectWhere(String sqlCondition, Object param1) {
        return selectWhere(sqlCondition, new Object[]{param1});
    }

    public List<E> selectWhere(String sqlCondition, Object param1, Object param2) {
        return selectWhere(sqlCondition, new Object[]{param1, param2});
    }

    public List<E> selectWhere(String sqlCondition, Object param1, Object param2, Object param3) {
        return selectWhere(sqlCondition, new Object[]{param1, param2, param3});
    }

    private List<E> selectWhere(String sqlCondition, Object[] values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;
        return namedParameterJdbcTemplate.getJdbcOperations()
                .query(sql, values, new BeanPropertyRowMapper<>(entityClass));
    }


    public List<E> selectWhere(String sqlCondition, SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;
        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(entityClass));
    }

    ////////////////////////////////////count///////////////////////////////////////////

    public int countWhere(String sqlCondition, Object param1) {
        return countWhere(sqlCondition, new Object[]{param1});
    }

    public int countWhere(String sqlCondition, Object param1, Object param2) {
        return countWhere(sqlCondition, new Object[]{param1, param2});
    }

    public int countWhere(String sqlCondition, Object param1, Object param2, Object param3) {
        return countWhere(sqlCondition, new Object[]{param1, param2, param3});
    }

    private int countWhere(String sqlCondition, Object[] values) {
        String sql = "SELECT count(*) FROM " + tableName + " WHERE " + sqlCondition;
        return namedParameterJdbcTemplate.getJdbcOperations().queryForObject(sql, values, Integer.class);
    }

    public int countWhere(String sqlCondition, SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT count(*) FROM " + tableName + " WHERE " + sqlCondition;
        return namedParameterJdbcTemplate.queryForObject(sql, parameterSource, Integer.class);
    }


    public int count() {
        /*
        String sql = "SELECT count(*) FROM " + tableName;
        return namedParameterJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class);
        */
        return sqlFactory.createSQL()
                .SELECT("COUNT(*)")
                .FROM(tableName)
                .queryInteger();
    }


    ////////////////page///////////////

    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object param1) {
        return selectPageWhere(sqlCondition, pageNumber, perPage, new Object[]{param1});
    }

    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object param1, Object param2) {
        return selectPageWhere(sqlCondition, pageNumber, perPage, new Object[]{param1, param2});
    }

    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object param1, Object param2, Object param3) {
        return selectPageWhere(sqlCondition, pageNumber, perPage, new Object[]{param1, param2, param3});
    }

    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object[] values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;
        List<E> list = namedParameterJdbcTemplate.getJdbcOperations().query(
                PageUtils.getRowsSQL(sql, pageNumber, perPage, this.dataSourceType),
                values,
                new BeanPropertyRowMapper<>(entityClass)
        );
        Integer count = namedParameterJdbcTemplate.getJdbcOperations().queryForObject(
                PageUtils.getNumberSQL(sql),
                Integer.class, values);
        return new ResultPage<>(list, count);
    }


    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage,
                                         SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE 1=1 AND " + sqlCondition;

        List<E> coll = namedParameterJdbcTemplate.query(
                PageUtils.getRowsSQL(sql, pageNumber, perPage, this.dataSourceType),
                parameterSource,
                new BeanPropertyRowMapper<>(entityClass)
        );
        Integer count = namedParameterJdbcTemplate.queryForObject(
                PageUtils.getNumberSQL(sql),
                parameterSource,
                Integer.class);

        return new ResultPage<>(coll, count);
    }


    public ResultPage<E> selectPage(int pageNumber, int perPage) {
        //sql
        String sql = "SELECT * FROM " + tableName;

        List<E> coll = namedParameterJdbcTemplate.query(
                PageUtils.getRowsSQL(sql, pageNumber, perPage, this.dataSourceType),
                EmptySqlParameterSource.INSTANCE,
                new BeanPropertyRowMapper<>(entityClass)
        );
        Integer count = namedParameterJdbcTemplate.queryForObject(
                PageUtils.getNumberSQL(sql),
                EmptySqlParameterSource.INSTANCE,
                Integer.class);
        return new ResultPage<>(coll, count);
    }


    ////////////////////////////////////拦截器///////////////////////////


    protected void beforeInsert(E entity) {
    }

    protected void afterInsert(E entity, int count) {
        if (count < 1) {
            log.warn(this.entityClass.getSimpleName() + "插入成功数量" + count + ",entity=" + entity.toString());
        }
    }

    protected void beforeUpdate(E entity) {
    }

    protected void afterUpdate(E entity, int count) {
        if (count < 1) {
            log.warn(this.entityClass.getSimpleName() + "更新成功数量" + count + ",entity=" + entity.toString());
        }
    }

    protected void beforeDelete(ID id) {
    }

    protected void afterDelete(ID id, int count) {
        if (count < 1) {
            log.warn(this.entityClass.getSimpleName() + "删除成功数量" + count + ",id=" + id);
        }
    }
}


