package top.fastsql.util;

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

    /**
     * 获取指定实体类对应的表名
     *
     * @param entityClass 实体类的类型令牌
     * @return 若指定的类中含有{@code javax.persistence.Table}注解，则返回注解的name字段的值
     */
    public static String getTableNameFromEntityClass(Class<?> entityClass) {
        //获取类名
        final String className = entityClass.getSimpleName();
        //通过将类名由驼峰转为蛇形获取表名
        String tableName = StringExtUtils.camelToUnderline(className);
        //获取实体类中的Table注解实例
        final Table table = entityClass.getAnnotation(Table.class);
        //判断实例是否非空
        if (table != null) {
            if (!StringUtils.isEmpty(table.name())) {
                tableName = table.name();
            }
        }
        //返回表名
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
            Method method = object.getClass().getDeclaredMethod(methodStr, new Class[]{});
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
            Field field = object.getClass().getDeclaredField(fieldStr);
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
}
