# 轻量级数据库访问框架FastSQL


[![Maven central](https://maven-badges.herokuapp.com/maven-central/top.fastsql/fastsql/badge.svg)](https://maven-badges.herokuapp.com/maven-central/top.fastsql/fastsql)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

![logo](logo_s.jpg)

FastSQL一个基于spring-jdbc的简单ORM框架，它支持sql构建、sql执行、命名参数绑定、查询结果自动映射和通用DAO。
结合了Hibernate/JPA快速开发和Mybatis高效执行的优点。

你可以这样访问数据库：

```java
// 第一步.构建SqlFactory
SqlFactory sqlFactory = ...

// 第二步.获取SQL实例，构建sql语句并执行
SQL sql = ;
Student student = sqlFactory.createSQL()
                       .SELECT("*").FROM("student").WHERE("id=:id") //拼接sql语句
                       .mapItemsParameter("id",101) //绑定参数
                       .queryOne(Student.class); //执行并映射结果

```


## 要点

* [使用文档](http://fastsql.top)
* [版本下载](https://oss.sonatype.org/content/repositories/releases/top/fastsql/fastsql/)