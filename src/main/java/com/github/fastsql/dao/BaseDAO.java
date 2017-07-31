package com.github.fastsql.dao;

import com.github.fastsql.dto.DbPageResult;
import com.github.fastsql.util.FastSqlUtils;
import com.github.fastsql.util.PageSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.StringUtils;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈佳志
 */
public abstract class BaseDAO<E, ID> {
    protected Class<E> entityClass;

    protected Logger logger;

    private String className;
    private String tableName;
    /**
     * cache
     */
    private String idName;
    private Method idGetMethod;

    private List<String> columnNamesWithodId = new ArrayList<>();
    private List<Method> getterMethodsWithoutId = new ArrayList<>();

    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setTemplate(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public NamedParameterJdbcTemplate getTemplate() {
        return template;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BaseDAO() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            this.entityClass = (Class<E>) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            this.entityClass = null;
        }
        logger = LoggerFactory.getLogger(entityClass);

        className = entityClass.getSimpleName();
        Table tableName = entityClass.getAnnotation(Table.class);
        if (tableName != null) {
            if (StringUtils.isEmpty(tableName.name())) {
                throw new RuntimeException(entityClass + "的注解@Table的name不能为空");
            }
            this.tableName = tableName.name();
        } else {
            this.tableName = FastSqlUtils.camelToUnderline(className);
        }

    }

    /////////////////////////////修改 /////////////////////////////////////////////

    /**
     * 全更新 null值在 数据库中设置为null
     */
    public int update(E entity) {
        initCache(entity);

        ID id;
        try {
            Method getId = entity.getClass().getMethod("getId", new Class[]{});
            id = (ID) getId.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("保存失败， getId() 方法不存在或调用失败");
        }
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("修改时对象id不能为空");
        }

        StringBuilder sqlBuilder = new StringBuilder();
        for (Method method : getterMethodsWithoutId) {
            try {
                Object value = method.invoke(entity);
                String columnName = FastSqlUtils.getterMethodNameToColumn(method.getName());
                String fieldName = FastSqlUtils.getterMethodNameToFieldName(method.getName());

                if (value != null) {
                    sqlBuilder.append("," + columnName + "=:" + fieldName);
                } else {
                    sqlBuilder.append("," + columnName + "=NULL");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String setValueSql = sqlBuilder.toString().replaceFirst(",", "");
        String sql = "UPDATE " + tableName + " SET " + setValueSql + " WHERE id=:id";//set sql


        int rows = template.update(
                sql, new BeanPropertySqlParameterSource(entity)
        );

        return rows;
    }


    /**
     * 仅更新非null， null值 不更新
     */
    public int updateIgnoreNull(E entity) {
        initCache(entity);

        ID id;
        try {
            Method getId = entity.getClass().getMethod("getId", new Class[]{});
            id = (ID) getId.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("保存失败， getId() 方法不存在或调用失败");
        }
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("修改时对象id不能为空");
        }

        StringBuilder builder = new StringBuilder();
        for (Method method : getterMethodsWithoutId) {
            try {
                Object value = method.invoke(entity);
                if (value != null) {
                    builder.append("," + getSingleEqualsStr(method));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String sql = "UPDATE " + tableName + " SET " + builder.toString().replaceFirst(",", "") + " WHERE id=:id";

        return template.update(
                sql, new BeanPropertySqlParameterSource(entity)
        );
    }


    private void initCache(E object) {
        if (getterMethodsWithoutId.size() > 0) {//lazy init
            this.getterMethodsWithoutId = FastSqlUtils.getAllGetterWithoutId(object);
            for (Method method : getterMethodsWithoutId) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.getClass().isAnnotationPresent(Id.class)) {
                        logger.info("id---" + method.getName());
                    }
                }

            }
        }

    }

    /////////////////////////////////////////////////保存方法////////////////////////////////////////

    /**
     * 插入对象中非null的值到数据库
     */
    public int saveIgnoreNull(E object) {
        initCache(object);

        StringBuilder nameBuilder = new StringBuilder("id");
        StringBuilder valueBuilder = new StringBuilder(":id");

        for (Method method : getterMethodsWithoutId) {
            try {
                Object value = method.invoke(object);
                if (value != null) {
                    String str = method.getName().replace("get", "");
                    String columnName = FastSqlUtils.camelToUnderline(str);
                    String fieldName = str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
                    nameBuilder.append("," + columnName);
                    valueBuilder.append(",:" + fieldName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String sql = "INSERT INTO " + tableName +
                "(" + nameBuilder.toString() + ") " +
                " VALUES " +
                "(" + valueBuilder.toString() + ")";

        int saveNum = template.update(
                sql, new BeanPropertySqlParameterSource(object)
        );

        return saveNum;
    }

    /**
     * 插入对象中的值到数据库，null值在数据库中会设置为NULL
     */
    public int save(E object) {
        initCache(object);

        StringBuilder nameBuilder = new StringBuilder("id");
        StringBuilder valueBuilder = new StringBuilder(":id");

        for (Method method : getterMethodsWithoutId) {
            try {
                Object value = method.invoke(object);
                String str = method.getName().replace("get", "");
                String columnName = FastSqlUtils.camelToUnderline(str);
                String fieldName = str.substring(0, 1).toLowerCase() + str.substring(1, str.length());

                nameBuilder.append("," + columnName);
                valueBuilder.append(",:" + fieldName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String sql = "INSERT INTO " + tableName + "(" + nameBuilder.toString() + ") VALUES(" + valueBuilder.toString() + ")";
        return template.update(sql, new BeanPropertySqlParameterSource(object));
    }

//    /**
//     * 获取保存对象的Id
//     */
//    private String getSaveId(E object) {
//        String id;
//        try {
//            Method getId = object.getClass().getMethod("getId", new Class[]{});
//            id = (String) getId.invoke(object);
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException("保存失败， getId() 方法不存在或调用失败");
//        }
//        if (StringUtils.isEmpty(id)) {
//            id = UUID.randomUUID().toString();
//        }
//        return id;
//    }

    /////////////////////////////////////////////////删除方法////////////////////////////////////////

    /**
     * 根据id删除数据
     */
    public int delete(String id) {
        //sql
        String sql = "DELETE FROM " + tableName + " WHERE id=:id";
        //参数
        Map<String, Object> map = FastSqlUtils.mapOf("id", id);
        return template.update(sql, map);
    }

    /**
     * 删除所有数据
     */
    public int deleteAll() {
        logger.warn(tableName + "#deleteAll()删除该表所有数据");
        //sql
        String sql = "DELETE FROM " + tableName;
        //参数
        return template.update(sql, new HashMap<String, Object>());
    }

    /**
     * 根据id列表批量删除数据
     */
    public int deleteInBatch(List<String> ids) {
        String sql = "DELETE FROM " + tableName + " WHERE id=:id";

        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            parameterSources[i] = new MapSqlParameterSource("id", ids.get(i));
        }

        int[] ints = template.batchUpdate(sql, parameterSources);
        int row = 0;
        for (int i : ints) {
            if (i == 1) {
                row++;
            }
        }
        return row;
    }


//    /**
//     * 根据Map更新
//     */
//    public int update(String id, Map<String, Object> updateColumnMap) {
//
//
//        StringBuilder sqlBuilder = new StringBuilder();
//
//        for (Map.Entry<String, Object> entry : updateColumnMap.entrySet()) {
//            String column = FastSqlUtils.camelToUnderline(entry.getKey());
//            if (entry.getValue() != null) {
//                sqlBuilder.append("," + column + "=:" + entry.getKey());
//            } else {
//                sqlBuilder.append("," + column + "=NULL");
//            }
//        }
//        updateColumnMap.put("id", id);
//        String sql = "UPDATE " + tableName + " SET " +
//                sqlBuilder.toString().replaceFirst(",", "") +
//                " WHERE id=:id";
//
//
//        return template.update(sql, updateColumnMap);
//    }

//    /**
//     * 根据Sql筛选更新
//     */
//    public int updateWhere(Map<String, Object> updateColumnMap, String condition, Map<String, Object> conditionMap) {
//
//
//        StringBuilder sqlBuilder = new StringBuilder();
//
//        for (Map.Entry<String, Object> entry : updateColumnMap.entrySet()) {
//            String column = FastSqlUtils.camelToUnderline(entry.getKey());
//            if (entry.getValue() != null) {
//                sqlBuilder.append("," + column + "=:" + entry.getKey());
//            } else {
//                sqlBuilder.append("," + column + "=NULL");
//            }
//        }
//        updateColumnMap.putAll(conditionMap);
//        String sql = "UPDATE " + tableName + " SET " +
//                sqlBuilder.toString().replaceFirst(",", "") +
//                " WHERE  " + condition;
//
//
//        return template.update(sql, updateColumnMap);
//    }


    //////////////////////////////find one/////////////////////////////////////

    /**
     * 通过id查找
     */
    public E findOne(String id) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE id=:id";
        //参数
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        E returnObject;
        try {
            returnObject = template.queryForObject(
                    sql, map, new BeanPropertyRowMapper<E>(entityClass)
            );
        } catch (EmptyResultDataAccessException e) {
            logger.warn(tableName + "#findOne()返回的数据为null");
            returnObject = null;
        }
        return returnObject;
    }

    /**
     * 通过where条件查找一条记录
     * 查找姓名为1年龄大于23的记录  findOneWhere("name=?1 and age>?2", "wang",23)
     *
     * @param sqlCondition name=:1 and age=:2
     * @param values       "wang",23
     */
    public E findOneWhere(String sqlCondition, Object... values) {
        if (sqlCondition == null) {
            throw new RuntimeException("sql不能为空");
        }

        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition.replaceAll("\\?", ":");

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramMap.put("" + (i + 1), values[i]);
        }

        List<E> dateList = template.query(
                sql, paramMap, new BeanPropertyRowMapper<E>(entityClass)
        );
        if (dateList.size() == 0) {
            logger.warn(tableName + "#findOneWhere()返回的数据为null");
            return null;
        } else if (dateList.size() == 1) {
            return dateList.get(0);
        } else {
            logger.error(tableName + "#findOneWhere()返回多条数据");
            throw new RuntimeException(tableName + "#findOneWhere()返回多条数据");
        }
    }
    //////////////////////////////find list/////////////////////////////////////

    //    /**
//     * 将实体中不为空字段的作为条件进行查询
//     */
//    public List<E> findListByPresentFields(E object) {
//        //sql
//        String sql = getSelectAllSqlFromEntity(object);
//        logger.debug(sql);
//        logger.debug(object.toString());
//
//        List<E> dateList = template.query(
//                sql, new BeanPropertySqlParameterSource(object), new BeanPropertyRowMapper<E>(entityClass)
//        );
//        return dateList;
//    }
    public List<E> findList() {
        //sql
        String sql = "SELECT * FROM " + tableName;
        return template.query(sql, new HashMap<String, Object>(), new BeanPropertyRowMapper<E>(entityClass));
    }

    public List<E> findListWhere(String sqlCondition, Object... values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition.replaceAll("\\?", ":");

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramMap.put("" + (i + 1), values[i]);
        }

        return template.query(sql, paramMap, new BeanPropertyRowMapper<E>(entityClass));
    }

    public List<E> findListWhere(String sqlCondition, BeanPropertySqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        return template.query(sql, parameterSource, new BeanPropertyRowMapper<E>(entityClass));
    }

    public List<E> findListWhere(String sqlCondition, Map<String, Object> parameterMap) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        return template.query(sql, parameterMap, new BeanPropertyRowMapper<E>(entityClass));
    }

    ////////////////////////////////////count///////////////////////////////////////////


    public int countWhere(String sqlCondition, Object... values) {
        //sql
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + sqlCondition.replaceAll("\\?", ":");

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramMap.put("" + (i + 1), values[i]);
        }
        return template.queryForObject(sql, paramMap, Integer.class);
    }

    ////////////////page///////////////


    public DbPageResult<E> findPageWhere(int pageNumber, int perPage, String sqlCondition, Object... values) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE " + sqlCondition;

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            paramMap.put("" + (i + 1), values[i]);
        }

        List<E> coll = template.query(
                PageSqlUtils.getRowsSQL(sql, pageNumber, perPage),
                paramMap,
                new BeanPropertyRowMapper<E>(entityClass)
        );
        Integer count = template.queryForObject(
                PageSqlUtils.getNumberSQL(sql),
                paramMap,
                Integer.class);

        return new DbPageResult<>(coll, count);
    }


    public DbPageResult<E> findPageWhere(int pageNumber, int perPage, String sqlCondition,
                                         BeanPropertySqlParameterSource parameterSource) {
        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE 1=1 AND " + sqlCondition;

        List<E> coll = template.query(
                PageSqlUtils.getRowsSQL(sql, pageNumber, perPage),
                parameterSource,
                new BeanPropertyRowMapper<E>(entityClass)
        );
        Integer count = template.queryForObject(
                PageSqlUtils.getNumberSQL(sql),
                parameterSource,
                Integer.class);

        return new DbPageResult<>(coll, count);
    }

    public DbPageResult<E> findPageWhere(int pageNumber, int perPage, String sqlCondition,
                                         Map<String, Object> parameterMap) {

        //sql
        String sql = "SELECT * FROM " + tableName + " WHERE 1=1 AND" + sqlCondition;

        List<E> coll = template.query(
                PageSqlUtils.getRowsSQL(sql, pageNumber, perPage),
                parameterMap,
                new BeanPropertyRowMapper<E>(entityClass)
        );
        Integer count = template.queryForObject(
                PageSqlUtils.getNumberSQL(sql),
                parameterMap,
                Integer.class);

        return new DbPageResult<>(coll, count);
    }

    ///////////////////////////////////////////////BYSQL///////////////////////////

    public Map<String, Object> queryMapBySql(String sql) {
        return template.queryForMap(sql, new HashMap<>());
    }

    public Map<String, Object> queryMapBySql(String sql, SqlParameterSource paramSource) {
        return template.queryForMap(sql, paramSource);
    }

    public Map<String, Object> queryMapBySql(String sql, Map<String, ?> paramMap) {
        return template.queryForMap(sql, paramMap);
    }


    public List<Map<String, Object>> queryMapListBySql(String sql) {
        return template.queryForList(sql, new HashMap<>());
    }

    public List<Map<String, Object>> queryMapListBySql(String sql, SqlParameterSource paramSource) {
        return template.queryForList(sql, paramSource);
    }

    public List<Map<String, Object>> queryMapListBySql(String sql, Map<String, ?> paramMap) {
        return template.queryForList(sql, paramMap);
    }


    public <T> T queryObjectBySql(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) {
        return template.queryForObject(sql, paramMap, rowMapper);
    }

    public <T> T queryObjectBySql(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) {
        return template.queryForObject(sql, paramSource, rowMapper);
    }

    public <T> List<T> queryListBySql(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) {
        return template.query(sql, paramMap, rowMapper);
    }

    public <T> List<T> queryListBySql(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) {
        return template.query(sql, paramSource, rowMapper);
    }

    public <T> DbPageResult<T> queryPageBySql(String baseSql, int pageNumber, int perPage,
                                              BeanPropertySqlParameterSource parameterSource,
                                              RowMapper<T> rowMapper) {
        //sql
        List<T> column = template.query(
                PageSqlUtils.getRowsSQL(baseSql, pageNumber, perPage),
                parameterSource,
                rowMapper);
        Integer count = template.queryForObject(
                PageSqlUtils.getNumberSQL(baseSql),
                parameterSource,
                Integer.class);

        return new DbPageResult<>(column, count);
    }

    public <T> DbPageResult<T> queryPageBySql(String baseSql, int pageNumber, int perPage,
                                              Map<String, ?> paramMap, RowMapper<T> rowMapper) {
        //sql
        List<T> column = template.query(
                PageSqlUtils.getRowsSQL(baseSql, pageNumber, perPage),
                paramMap,
                rowMapper);
        Integer count = template.queryForObject(
                PageSqlUtils.getNumberSQL(baseSql),
                paramMap,
                Integer.class);

        return new DbPageResult<>(column, count);
    }

    private String getSingleEqualsStr(Method method) {
        String str = method.getName().replace("get", "");
        String columnName = FastSqlUtils.camelToUnderline(str);
        String fieldName = str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
        return columnName + "=:" + fieldName;
    }

    //bySql
}


