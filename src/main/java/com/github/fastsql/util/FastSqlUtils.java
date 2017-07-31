package com.github.fastsql.util;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 内部工具类
 *
 * @author 陈佳志
 */
public class FastSqlUtils {

    public static final char UNDERLINE = '_';

    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        String temp = sb.toString();
        if (temp.startsWith("_")) {
            return temp.substring(1);
        }
        return temp;

    }

    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String getterMethodNameToColumn(String methodName) {
        String str = methodName.replace("get", "");
        return camelToUnderline(str);
    }

    public static String getterMethodNameToFieldName(String methodName) {


        String str = methodName.replace("get", "");

//        String str = method.getName().replace("get", "");
//        String columnName = FastSqlUtils.getterMethodNameToColumn(method.getName());
        String fieldName = str.substring(0, 1).toLowerCase() + str.substring(1, str.length());

        return fieldName;
    }

    public static String underlineToCamelFirstLower(String param) {
        String str = underlineToCamel(param);
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String getStringUuid() {

        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public static List<Method> getAllGetter(Object object) {
        Method[] methods = object.getClass().getDeclaredMethods();

        return Arrays.asList(methods);
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

        for (Field field : declaredFields) {

            if (!field.isAnnotationPresent(Id.class)) {
                fieldList.add(field);
            }


        }
        return fieldList;
    }

    public static List<Field> getAllField(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(declaredFields.length);

        for (Field field : declaredFields) {
            fieldList.add(field);
        }
        return fieldList;
    }

    public static Map<String, Object> emptyHashMap() {

        return new HashMap<String, Object>();
    }

    public static Map<String, Object> mapOf(String k1, Object v1) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(k1, v1);
        return hashMap;
    }

    public static String getStringInClause(List<String> strings) {
        StringBuilder builder = new StringBuilder("( ");
        for (String string : strings) {
            builder.append(", '" + string + "'");
        }
        builder.append(" )");
        return builder.toString().replaceFirst(",", "");
    }

    public static String getIntegerInClause(List<Integer> integers) {
        StringBuilder builder = new StringBuilder("( ");
        for (Integer i : integers) {
            builder.append(",  " + i + " ");
        }
        builder.append(" )");
        return builder.toString().replaceFirst(",", "");
    }
}