package top.fastsql.dao;

import top.fastsql.SQL;
import top.fastsql.SQLFactory;
import top.fastsql.config.DataSourceType;
import top.fastsql.dto.BatchUpdateResult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 实体类的元数据
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

    /**
     * 执行引擎
     */
    protected SQLFactory sqlFactory;


    protected SQLFactory getSqlFactory() {
        if (sqlFactory == null) {
            throw new IllegalArgumentException("sqlFactory is null in BaseDAO,please set it.");
        }
        return sqlFactory;
    }

    protected SQL getSQL() {
        if (sqlFactory == null) {
            throw new IllegalArgumentException("sqlFactory is null in BaseDAO,please set it.");
        }
        return sqlFactory.createSQL();
    }

    @Autowired
    public void setSqlFactory(SQLFactory sqlFactory) {
        this.sqlFactory = sqlFactory;
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

        this.fieldsWithoutId.forEach(field -> this.columnNamesWithoutId.add(camelToUnderline(field.getName())));
        //主键
        this.idField = EntityRefelectUtils.getIdField(entityClass); //TODO 2
        this.idColumnName = camelToUnderline(idField.getName());

        //所有
        this.fields.addAll(this.fieldsWithoutId);
        this.fields.add(0, this.idField);
        this.columnNames.addAll(this.columnNamesWithoutId);
        this.columnNames.add(0, camelToUnderline(this.idField.getName()));
    }


    /////////////////////////////////////////////////保存方法////////////////////////////////////////

    /**
     * 插入对象中非null的值到数据库
     *
     * @param entity 实体类对象
     * @return 插入成功的数量
     */
    public int insertSelective(E entity) {
        if (useBeforeInsert) {
            beforeInsert(entity);
        }
        //SQL语句部分字符串构建器
        final StringBuilder nameBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
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

        final int count = sql.update();
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
        if (useBeforeInsert) {
            beforeInsert(entity);
        }

        final StringBuilder nameBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        fields.forEach(field -> {
            nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
            valueBuilder.append(",:").append(field.getName());
        });

        final SQL sql = sqlFactory.createSQL()
                .INSERT_INTO(tableName, nameBuilder.deleteCharAt(0).toString())
                .VALUES(valueBuilder.deleteCharAt(0).toString())
                .beanParameter(entity);


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
        if (useBeforeUpdate) {
            beforeUpdate(entity);
        }
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

        final SQL sql = sqlFactory.createSQL()
                .UPDATE(tableName)
                .SET(sqlBuilder.deleteCharAt(0).toString())
                .WHERE(idColumnName + "=:" + idColumnName)
                .beanParameter(entity);


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
        if (useBeforeUpdate) {
            beforeUpdate(entity);
        }
        final ID id = (ID) EntityRefelectUtils.getFieldValue(entity, idField);
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("修改时对象id不能为空");
        }
        final StringBuilder sqlBuilder = new StringBuilder();
        fieldsWithoutId.forEach(field ->
                sqlBuilder.append("," + StringExtUtils.camelToUnderline(field.getName()) +
                        "=:" +
                        field.getName()));

        final SQL sql = sqlFactory.createSQL()
                .UPDATE(tableName)
                .SET(sqlBuilder.deleteCharAt(0).toString())
                .WHERE(idColumnName + "=:" + idColumnName)
                .beanParameter(entity);


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
        return sqlFactory.createSQL().useSql("DELETE FROM " + tableName).update();
    }

    /**
     * 根据条件删除
     */
    public int deleteWhere(String sqlCondition, Object... values) {
        String sql = "DELETE FROM " + tableName + " WHERE " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).varParameter(values).update();
    }

    /**
     * 根据id列表批量删除数据
     */
    public BatchUpdateResult deleteInBatch(List<ID> ids) {
        final String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + "=:" + idColumnName;

        List<Map<String, Object>> mapList = new ArrayList<>(ids.size());
        for (ID id : ids) {
            Map<String, Object> map = new HashMap<>();
            map.put(idColumnName, id);
            mapList.add(map);
        }

        return sqlFactory.createSQL().useSql(sql).batchUpdateByMapParams(mapList);
    }


    //////////////////////////////find one/////////////////////////////////////

    /**
     * 通过id查找
     */
    public E selectOneById(ID id) {
        E returnObject;
        try {

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



    /**
     * 通过where条件查找一条记录
     * 查找姓名为1年龄大于23的记录  selectOneWhere("name=? and age>?", "wang",23)
     *
     * @param sqlCondition name=:1 and age=:2
     * @param values       "wang",23
     */
    public E selectOneWhere(String sqlCondition, Object... values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        List<E> dataList = getSqlFactory().createSQL().useSql(sql)
                .varParameter(values)
                .queryList(new BeanPropertyRowMapper<>(entityClass));


        if (dataList.isEmpty()) {
            return null;
        } else if (dataList.size() == 1) {
            return dataList.get(0);
        } else {
            log.error(tableName + "#findOneWhere()返回多条数据");
            throw new RuntimeException(tableName + "#findOneWhere()返回多条数据");
        }
    }

    public E selectOneWhere(String sqlCondition, SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        List<E> dataList = getSqlFactory().createSQL().useSql(sql)
                .parameter(parameterSource)
                .queryList(new BeanPropertyRowMapper<>(entityClass));


        if (dataList.size() == 0) {
            return null;
        } else if (dataList.size() == 1) {
            return dataList.get(0);
        } else {
            log.error(tableName + "#findOneWhere()返回多条数据");
            throw new RuntimeException(tableName + "#findOneWhere()返回多条数据");
        }
    }

    //////////////////////////////find list/////////////////////////////////////

    public List<E> selectAll() {
        return sqlFactory.createSQL().SELECT("*").FROM(tableName).queryList(entityClass);
    }

    public List<E> selectWhere(String sqlCondition, Object... values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).varParameter(values).queryList(new BeanPropertyRowMapper<>(entityClass));
    }

    public List<E> selectWhere(String sqlCondition, SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).parameter(parameterSource).queryList(new BeanPropertyRowMapper<>(entityClass));
    }

    ////////////////////////////////////count///////////////////////////////////////////
    public int countWhere(String sqlCondition, Object... values) {
        String sql = "SELECT count(*) FROM " + tableName + " WHERE " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).varParameter(values).queryInteger();
    }

    public int countWhere(String sqlCondition, SqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT count(*) FROM " + tableName + " WHERE " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).parameter(parameterSource).queryInteger();
    }

    public int count() {
        return sqlFactory.createSQL().SELECT("COUNT(*)").FROM(tableName).queryInteger();
    }

    //////////////////////////////////query page///////////////////////////////////////////////////////////////
    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object... values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).varParameter(values)
                .queryPage(pageNumber, perPage, new BeanPropertyRowMapper<>(entityClass));
    }

    public ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage,
                                         SqlParameterSource parameterSource) {
        String sql = "SELECT * FROM " + tableName + " WHERE 1=1 AND " + sqlCondition;
        return sqlFactory.createSQL().useSql(sql).parameter(parameterSource)
                .queryPage(pageNumber, perPage, new BeanPropertyRowMapper<>(entityClass));
    }


    public ResultPage<E> selectPage(int pageNumber, int perPage) {
        String sql = "SELECT * FROM " + tableName;
        return sqlFactory.createSQL().useSql(sql)
                .queryPage(pageNumber, perPage, new BeanPropertyRowMapper<>(entityClass));
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


