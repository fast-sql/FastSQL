package org.fastsql.util;

import org.springframework.util.StringUtils;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 实体类的反射工具
 *
 * @author 陈佳志
 * 2017-09-03
 */
public class EntityRefelectUtils {

    public static String getTableNameFromEntityClass(Class<?> entityClass) {
        String className = entityClass.getSimpleName();

        String tableName = StringExtUtils.camelToUnderline(className);

        Table table = entityClass.getAnnotation(Table.class);

        if (table != null) {
            if (!StringUtils.isEmpty(table.name())) {
                tableName = table.name();
            }
        }

        return tableName;
    }

    public static String getterMethodNameToColumn(String methodName) {
        String str = methodName.replace("get", "");
        return StringExtUtils.camelToUnderline(str);
    }

    public static String getterMethodNameToFieldName(String methodName) {
        String str = methodName.replace("get", "");
        return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
    }

    public static String underlineToCamelFirstLower(String param) {
        String str = StringExtUtils.underlineToCamel(param);
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static List<Method> getAllGetter(Object object) {
        Method[] methods = object.getClass().getDeclaredMethods();

        return Arrays.asList(methods);
    }

    public static Object invokeMethod(Object object, String methodStr) {
        try {
            Method method = object.getClass().getMethod(methodStr, new Class[]{});
            return method.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Object invokeMethod(Object object, Method method) {
        try {
            return method.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象的字段值
     *
     * @param object   对象
     * @param fieldStr 对象属性的字符串名称
     */
    public static Object getFieldValue(Object object, String fieldStr) {
        try {
            Field field = object.getClass().getField(fieldStr);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象的字段值
     *
     * @param object 对象
     * @param field  对象属性
     */
    public static Object getFieldValue(Object object, Field field) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object object, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object object, String fieldStr, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldStr);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Method> getAllGetterWithoutId(Object object) {
        List<Method> methodList = new ArrayList<>();
        Method[] methods = object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get") &&
                    (!method.getName().equals("getId"))) {
                methodList.add(method);
            }

        }
        return methodList;
    }

    public static List<Field> getAllFieldWithoutId(Object object) {
        List<Field> fieldList = new ArrayList<>();
        Field[] declaredFields = object.getClass().getDeclaredFields();

        boolean containId = false;

        for (Field field : declaredFields) {
            if (!field.isAnnotationPresent(Id.class)) {
                fieldList.add(field);
            } else {
                containId = true;
            }
        }
        if (!containId) {
            throw new RuntimeException(object.getClass().getSimpleName() + " must with a @Id field");
        }

        return fieldList;
    }

    public static List<Field> getAllFieldWithoutIdByClass(Class cls) {
        List<Field> fieldList = new ArrayList<>();
        Field[] declaredFields = cls.getDeclaredFields();

        boolean containId = false;

        for (Field field : declaredFields) {
            if (!field.isAnnotationPresent(Id.class)) {
                fieldList.add(field);
            } else {
                containId = true;
            }
        }
        if (!containId) {
            throw new RuntimeException(cls.getSimpleName() + "实体类必须有一个包含@Id的字段");
        }

        return fieldList;
    }


    public static Field getIdField(Object object) {
        List<Field> fieldList = new ArrayList<>();
        Field[] declaredFields = object.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Id.class)) {
                fieldList.add(field);
            }
        }
        if (fieldList.size() == 0) {
            throw new RuntimeException(object.getClass().getSimpleName() + "实体类必须有一个包含@Id的字段");
        }
        if (fieldList.size() > 1) {
            throw new RuntimeException(object.getClass().getSimpleName() + "实体类必须有一个包含@Id的字段");
        }
        return fieldList.get(0);
    }

    public static Field getIdField(Class cls) {
        List<Field> fieldList = new ArrayList<>();
        Field[] declaredFields = cls.getDeclaredFields();


        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Id.class)) {
                fieldList.add(field);
            }
        }
        if (fieldList.size() == 0) {
            throw new RuntimeException(cls.getSimpleName() + "实体类必须有一个包含@Id的字段");
        }
        if (fieldList.size() > 1) {
            throw new RuntimeException(cls.getSimpleName() + "实体类必须有一个包含@Id的字段");
        }
        return fieldList.get(0);
    }

    public static List<Field> getAllField(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(declaredFields.length);

        for (Field field : declaredFields) {
            fieldList.add(field);
        }
        return fieldList;
    }

    public static List<Field> getAllField(Class cls) {
        Field[] declaredFields = cls.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(declaredFields.length);

        for (Field field : declaredFields) {
            fieldList.add(field);
        }
        return fieldList;
    }


    public static String getStringInClause(List strings) {
        StringBuilder builder = new StringBuilder("( ");
        for (Object string : strings) {
            builder.append(",'" + string.toString() + "'");
        }
        builder.append(" )");
        return builder.toString().replaceFirst(",", "");
    }

    public static String getIntegerInClause(List integers) {
        StringBuilder builder = new StringBuilder("( ");
        for (Object i : integers) {
            builder.append("," + i + " ");
        }
        builder.append(" )");
        return builder.toString().replaceFirst(",", "");
    }
}
