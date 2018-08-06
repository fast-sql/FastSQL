package top.fastsql.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Chenjiazhi
 * 2018-08-01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})//定义注解的作用目标**作用范围字段、枚举的常量/方法
public @interface InnerBeanMapped {

}
