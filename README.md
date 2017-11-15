
- [1. 简介](#1-%E7%AE%80%E4%BB%8B)
- [2. 入门](#2-%E5%85%A5%E9%97%A8)
    - [安装](#%E5%AE%89%E8%A3%85)
    - [构建 SQLFactory](#%E6%9E%84%E5%BB%BA-sqlfactory)
    - [从 SQLFactory 中获取 SQL](#%E4%BB%8E-sqlfactory-%E4%B8%AD%E8%8E%B7%E5%8F%96-sql)
    - [作用域（Scope）和生命周期](#%E4%BD%9C%E7%94%A8%E5%9F%9F%EF%BC%88scope%EF%BC%89%E5%92%8C%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F)
- [3. SQLFactory 配置](#3-sqlfactory-%E9%85%8D%E7%BD%AE)
- [4. SQL类作为sql语句构建器使用](#4-sql%E7%B1%BB%E4%BD%9C%E4%B8%BAsql%E8%AF%AD%E5%8F%A5%E6%9E%84%E5%BB%BA%E5%99%A8%E4%BD%BF%E7%94%A8)
    - [基本查询](#%E5%9F%BA%E6%9C%AC%E6%9F%A5%E8%AF%A2)
    - [使用操作符方法](#%E4%BD%BF%E7%94%A8%E6%93%8D%E4%BD%9C%E7%AC%A6%E6%96%B9%E6%B3%95)
    - [byType(Object)方法](#bytypeobject%E6%96%B9%E6%B3%95)
    - [使用连接查询/排序](#%E4%BD%BF%E7%94%A8%E8%BF%9E%E6%8E%A5%E6%9F%A5%E8%AF%A2%E6%8E%92%E5%BA%8F)
    - [分组查询](#%E5%88%86%E7%BB%84%E6%9F%A5%E8%AF%A2)
    - [IN语句](#in%E8%AF%AD%E5%8F%A5)
    - [使用$_$()方法进行子查询](#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95%E8%BF%9B%E8%A1%8C%E5%AD%90%E6%9F%A5%E8%AF%A2)
    - [AND和OR结合使用](#and%E5%92%8Cor%E7%BB%93%E5%90%88%E4%BD%BF%E7%94%A8)
    - [使用Lambda表达式简化构建动态sql](#%E4%BD%BF%E7%94%A8lambda%E8%A1%A8%E8%BE%BE%E5%BC%8F%E7%AE%80%E5%8C%96%E6%9E%84%E5%BB%BA%E5%8A%A8%E6%80%81sql)
    - [构建插入insert/修改update/删除delete语句](#%E6%9E%84%E5%BB%BA%E6%8F%92%E5%85%A5insert%E4%BF%AE%E6%94%B9update%E5%88%A0%E9%99%A4delete%E8%AF%AD%E5%8F%A5)
    - [分页功能](#%E5%88%86%E9%A1%B5%E5%8A%9F%E8%83%BD)
- [5. SQL构建器的执行功能](#5-sql%E6%9E%84%E5%BB%BA%E5%99%A8%E7%9A%84%E6%89%A7%E8%A1%8C%E5%8A%9F%E8%83%BD)
    - [创建SqlFactory](#%E5%88%9B%E5%BB%BAsqlfactory)
    - [设置参数方法](#%E8%AE%BE%E7%BD%AE%E5%8F%82%E6%95%B0%E6%96%B9%E6%B3%95)
    - [查询方法](#%E6%9F%A5%E8%AF%A2%E6%96%B9%E6%B3%95)
    - [增删改操作：](#%E5%A2%9E%E5%88%A0%E6%94%B9%E6%93%8D%E4%BD%9C%EF%BC%9A)
    - [获取数据库元信息](#%E8%8E%B7%E5%8F%96%E6%95%B0%E6%8D%AE%E5%BA%93%E5%85%83%E4%BF%A1%E6%81%AF)
    - [事务管理](#%E4%BA%8B%E5%8A%A1%E7%AE%A1%E7%90%86)
- [6.BaseDAO](#6basedao)
    - [6.1.数据准备](#61%E6%95%B0%E6%8D%AE%E5%87%86%E5%A4%87)
        - [Entity实体类](#entity%E5%AE%9E%E4%BD%93%E7%B1%BB)
        - [DAO类在Spring环境中](#dao%E7%B1%BB%E5%9C%A8spring%E7%8E%AF%E5%A2%83%E4%B8%AD)
        - [DAO类在非Spring环境中](#dao%E7%B1%BB%E5%9C%A8%E9%9D%9Espring%E7%8E%AF%E5%A2%83%E4%B8%AD)
    - [6.2.基本使用方法 CRUD](#62%E5%9F%BA%E6%9C%AC%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95-crud)
        - [数据插入](#%E6%95%B0%E6%8D%AE%E6%8F%92%E5%85%A5)
        - [数据修改](#%E6%95%B0%E6%8D%AE%E4%BF%AE%E6%94%B9)
        - [数据删除](#%E6%95%B0%E6%8D%AE%E5%88%A0%E9%99%A4)
        - [单条数据查询](#%E5%8D%95%E6%9D%A1%E6%95%B0%E6%8D%AE%E6%9F%A5%E8%AF%A2)
        - [多条数据查询](#%E5%A4%9A%E6%9D%A1%E6%95%B0%E6%8D%AE%E6%9F%A5%E8%AF%A2)
        - [分页查询](#%E5%88%86%E9%A1%B5%E6%9F%A5%E8%AF%A2)
        - [其他查询](#%E5%85%B6%E4%BB%96%E6%9F%A5%E8%AF%A2)
    - [定制你的ApplicationBaseDAO](#%E5%AE%9A%E5%88%B6%E4%BD%A0%E7%9A%84applicationbasedao)
        - [定制通用方法](#%E5%AE%9A%E5%88%B6%E9%80%9A%E7%94%A8%E6%96%B9%E6%B3%95)
        - [改变BaseDAO的默认属性](#%E6%94%B9%E5%8F%98basedao%E7%9A%84%E9%BB%98%E8%AE%A4%E5%B1%9E%E6%80%A7)
        - [设置多数据源支持](#%E8%AE%BE%E7%BD%AE%E5%A4%9A%E6%95%B0%E6%8D%AE%E6%BA%90%E6%94%AF%E6%8C%81)
    - [SQL构建器在BaseDAO中的使用](#sql%E6%9E%84%E5%BB%BA%E5%99%A8%E5%9C%A8basedao%E4%B8%AD%E7%9A%84%E4%BD%BF%E7%94%A8)
- [通用工具](#%E9%80%9A%E7%94%A8%E5%B7%A5%E5%85%B7)
    - [获取sql的IN列表](#%E8%8E%B7%E5%8F%96sql%E7%9A%84in%E5%88%97%E8%A1%A8)
- [配置项](#%E9%85%8D%E7%BD%AE%E9%A1%B9)

# 1. 简介
FastSQL一个基于spring-jdbc的简单ORM框架，它支持sql构建、sql执行、查询结果自动映射和通用DAO。结合了Hibernate/JPA快速开发和Mybatis高效执行的优点。

FastSQL可以完全满足你控制欲，可以用Java代码清晰又方便地写出sql语句并执行。 


# 2. 入门

## 安装

要使用 FastSQL， 只需将 fastsql-x.x.x.jar 文件置于 classpath 中即可（x.x.x为对应的版本号，下同）。

如果使用 Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml 文件中：

```
<dependency>
    <groupId>top.fastsql</groupId>
    <artifactId>fastsql</artifactId>
    <version>x.x.x</version>
</dependency>
```

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件的 dependencies 代码块中：
```
compile 'top.fastsql:fastsql:x.x.x'
```

## 构建 SQLFactory
你可以直接从 Java 程序构建一个 SQLFactory ，至少需要一个 DataSource 。
```
//新建一个DataSource（这里使用了Spring-Jdbc的SimpleDriverDataSource）
DataSource dataSource = new SimpleDriverDataSource([传入url,username等]);

SQLFactory sqlFactory = new SQLFactory();
sqlFactory.setDataSource(dataSource);
```

## 从 SQLFactory 中获取 SQL

既然有了 SQLFactory ，我们就可以从中获得 SQL 的实例了。SQL 完全包含了面向数据库执行 sql 命令所需的所有方法。
你可以通过 SQL 实例来构建并直接执行 SQL 语句。例如：
```
SQL sql = sqlFactory.createSQL();
Student student = sql.SELECT("*").FROM("student").WHERE("id=101").queryOne(Student.class);
```
## 作用域（Scope）和生命周期

**SQLFactory**

SQLFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由对它进行清除或重建。
使用 SQLFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SQLFactory 被视为一种代码“坏味道（bad smell）”。
因此 SQLFactory 的最佳作用域是应用的作用域。有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。

**SQL**

SQL 实例是有状态的 ，不是线程安全的，是不能被共享的。绝对不能将 SQL 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 
每执行SQL语句一次，都需要构建一个 SQL 实例，返回一个结果。

# 3. SQLFactory 配置

指定DataSource
````
sqlFactory.setDataSource(dataSource);
````
设置数据源类型
````
sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL);
sqlFactory.setDataSourceType(DataSourceType.MY_SQL);
sqlFactory.setDataSourceType(DataSourceType.ORACLE);
````

设置打印sql
```
sqlFactory.setLogSQLWhenBuild(true);

```

# 4. SQL类作为sql语句构建器使用
Java程序员面对的最痛苦的事情之一就是在Java代码中嵌入SQL语句。FastSQL提供`SQL`类简化你构建sql语句的过程。
```
SQLFactory sqlFactory = new SQLFactory();
```

## 基本查询
SELECT方法可以传入一个可变参数，以便选择多列。(FastSQL中建议SQL关键字全部采用大写)
```java
sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE("age>10").build();
//==> SELECT name,age FROM student WHERE age>10
sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE("name='小红'").build();
//==> SELECT name,age FROM student WHERE name='小红'
```
`WHERE()`关键字生成`WHERE 1=1`,如下
```java
SQL sql = sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE();
if (true){
  sql.AND("age > 10");
}
if (false){
  sql.AND("age < 8");
}

//===>SELECT name,age  FROM student  WHERE 1 = 1  AND age > 10 
```
## 使用操作符方法
FastSQL提供了一些操作符方便SQL的构建，比如：

```java
sqlFactory.createSQL()
    .SELECT("name", "age")
    .FROM("student")
    .WHERE("age").lt("10")
    .AND("name").eq("'小明'")
    .build();
//==> SELECT name,age FROM student WHERE age > 10 AND name = '小明'
```

如下：

| 方法             | 说明                                                 |
| :--------------- | :--------------------------------------------------- |
| eq(String)       | 生成=，并追加参数（equals的缩写）                    |
| gt(String)       | 生成>，并追加参数（是greater than的缩写）            |
| gtEq(String)     | 生成>=，并追加参数（是greater than or equals的缩写） |
| lt(String)       | 生成<，并追加参数（是less than的缩写 ）              |
| ltEq(String)     | 生成<=，并追加参数（是less than or equals的缩写）    |
| nEq(String)      | 生成!=，并追加参数（是not equals的缩写  ）           |
| LIKE(String)     | 生成LIKE 并追加参数，                                |
| NOT_LIKE(String) | 生成NOT LIKE ,并追加参数                             |
| NOT_LIKE(String) | 生成NOT LIKE ，并追加参数                            |
| IS_NULL()        | 生成IS NULL                                          |
| IS_NOT_NULL()    | 生成IS NOT NULL                                      |

注意：
- 这些方法仅仅是字符串连接：`eq("1")`生成` = 1` ，`eq("'1'")`会生成` = '1'`。
- 同样提供无参数方法 eq()/gt()/... 不会追加参数。

##  byType(Object)方法

操作符方法仅仅是字符串连接，`byType(Object)`方法会根据类型生成你想要的结果，上面的例子改写如下
```java
sqlFactory.createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("age").lt().byType(10)
                .AND("name").eq().byType("小明")
                .build();
//==>SELECT name,age FROM student WHERE age < 10 AND name = '小明'
```
eq().byType("小明") 也可简写为 eqByType("小明")

## 使用连接查询/排序

查询不及格的成绩

```java
String sql = sqlFactory.createSQL().SELECT("s.name","c.subject_name","c.score_value")
                .FROM("score c")
                .LEFT_JOIN_ON("student s", "s.id=c.student_id")
                .WHERE("c.score_value<60")
                .ORDER_BY("c.score_value")
                .build();
/*
SELECT s.name, c.subject,c.score_value
FROM score c
LEFT OUTER JOIN student s ON (s.id = c.student_id)
WHERE c.score_value < 60
ORDER BY c.score_value
*/
```
## 分组查询
查询每个学生总分数
```java
String sql =sqlFactory.createSQL().SELECT("s.name", "sum(c.score_value) total_score")
               .FROM("score c")
               .LEFT_JOIN_ON("student s", "s.id=c.student_id")
               .GROUP_BY("s.name")
               .build()
/*
SELECT s.name, sum(c.score_value) total_score
FROM score c
LEFT OUTER JOIN student s ON (s.id = c.student_id)
GROUP BY s.name
*/
```

## IN语句  
由于Jdbc规范不支持IN参数绑定，FastSQL提供了几种IN语句直接拼接的方式：
```java
//1.使用字符串
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN("('小明','小红')")
   .build();

//2.使用集合（List,Set等）
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN(Lists.newArrayList("小明","小红"))
   .build();

//3.使用数组
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN(new Object[]{"小明","小红"})//
   .build();

//输出===>SELECT *  FROM student  WHERE name  IN ('小明','小红')
```
## 使用$_$()方法进行子查询 
查询大于平均分的成绩（可以使用 $_$()方法）
```java
sqlFactory.createSQL().SELECT("*")
   .FROM("score")
   .WHERE("score_value >")
   .$_$(
         sqlFactory.createSQL().SELECT("avg(score_value)").FROM("score")
    )
   .build();
//SELECT *  FROM score  
//WHERE score_value >  ( SELECT avg(score_value)  FROM score  )
```
带有IN的子查询
```java
sqlFactory.createSQL().SELECT("*")
    .FROM("score")
    .WHERE()
    .AND("score")
    .IN().$_$(
         sqlFactory.createSQL().SELECT("DISTINCT score_value").FROM("score")
    )
    .build();
//SELECT * FROM score WHERE 1 = 1 AND score IN (SELECT DISTINCT score_value FROM score)
```

## AND和OR结合使用
如果查询年龄大于10岁，并且名字是小明或小红

```java
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND("(name='小明' OR name='小红')")//手动添加括号
   .build();
//或者
sqlFactory.createSQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND().$_$("name='小明' OR name='小红'")//$_$ 生成左右括号
   .build();
```

## 使用Lambda表达式简化构建动态sql
- `ifTrue(boolean bool, Consumer<SQL> sqlConsumer)`:如果第1个参数为true，则执行第二个参数（Lambda表达式）
- `ifNotEmpty(Collection<?> collection, Consumer<SQL> sqlConsumer)`:如果第1个参数长度大于0，则执行第二个参数（Lambda表达式）
- `ifPresent(Object object, Consumer<SQL> sqlConsumer)`:如果第1个参数存在（不等于null且不为""），则执行第二个参数（Lambda表达式）

```java
sqlFactory.createSQL().SELECT("student")
    .WHERE("id=:id")
    .ifTrue(true, thisBuilder -> thisBuilder.AND("name=:name"))  
    .ifNotEmpty(names, thisBuilder -> {
        System.out.println("ifNotEmpty?");
        thisBuilder.AND("name").IN(Lists.newArrayList("小明", "小红"));
    })
    .ifPresent("",thisBuilder -> {
        System.out.println("ifPresent?");
        //...处理其他流程语句...
    })
    .build();
```
输出：
```
ifNotEmpty?
SELECT student WHERE id=:id AND name=:name AND name  IN ('小明','小红')
```


## 构建插入insert/修改update/删除delete语句
**插入**

```java
//使用列
sqlFactory.createSQL().INSERT_INTO("student", "id", "name", "age")
                .VALUES(":id", ":name", ":age").build();
//=>INSERT INTO student (id,name,age)  VALUES (:id,:name,:age)

//不使用列
sqlFactory.createSQL().INSERT_INTO("student").VALUES(":id", ":name", ":age").build();
//=>INSERT INTO student VALUES (:id,:name,:age)
```

**修改**

SET(String column, String value) :SET关键字

setOne(String column, String value) :追加一个值


```java
sqlFactory.createSQL().UPDATE("student").SET("name","'Jack'").setOne("age","9").WHERE("name").eq("'Mike'").build();
//=>  UPDATE student SET name='Jack',age=9 WHERE name = 'Mike'              
```

**构建删除语句**

```java
sqlFactory.createSQL().DELETE_FROM("student").WHERE("id=:id").build();
//=>DELETE FROM student WHERE id=:id                
```

## 分页功能
**使用原生关键字进行分页**
```java
sqlFactory.createSQL().SELECT("*").FROM("student").LIMIT(10).build();
sqlFactory.createSQL().SELECT("*").FROM("student").LIMIT(5, 10).build();  //postgresql中的写法
sqlFactory.createSQL().SELECT("*").FROM("student").LIMIT(10).OFFSET(5).build(); //mysql中的写法
```
生成如下SQL
```sql
SELECT * FROM student LIMIT 10
SELECT * FROM student LIMIT 5,10
SELECT * FROM student LIMIT 10 OFFSET 5
```

**使用** `pageThis(int,int)` **分页方法进行分页**
```
//
sqlFactory.setDataSourceType(DataSourceType.POSTGRESQL); //使用枚举指定数据源类型
sqlFactory.createSQL().SELECT("*").FROM("student").pageThis(1,10).build();
```
注意：如果不指定 dataSourceType，将会使用 FastSQLConfig#dataSourceType 的默认类型进行分页;

**使用** `countThis()` **生成获取数量语句**
```java
//countThis
sqlFactory.createSQL().SELECT("*").FROM("student").countThis().buildAndPrintSQL();
```
 
# 5. SQL构建器的执行功能


##  创建SqlFactory
```java
//创建任意DataSource对象（这里使用了spring自带的数据源SimpleDriverDataSource）
DataSource dataSource = new SimpleDriverDataSource(
                new Driver(), "jdbc:postgresql://192.168.0.226:5432/picasso_dev2?stringtype=unspecified", 
                "developer", "password");

//创建SqlFactory
SqlFactory sqlFactory = new SqlFactory();
sqlFactory.setDataSource(dataSource);
sqlFactory.setDataSourceType(DataSourceType.MY_SQL);
```

##   设置参数方法
FastSQL支持多种传入命名参数的方法：

- `parameter(SqlParameterSource sqlParameterSource)` 支持传入SqlParameterSource类型的参数（兼容spring-jdbc）
- `beanParameter(Object dto)`方法可以传入对象参数
- `mapParameter(Map<String, Object> map)`支持传入Map类型参数
- `mapItemsParameter(Object... param)`支持多个key-value形式的参数，比如`mapItemsParameter("id", 12345,"name","小明")`
- `beanAndMapParameter(Object dto, Map<String, Object> map)` 支持两种不同的参数组合，后一个会覆盖前面的相同名字的参数
- `addParameterMapItem(String key, Object value)`可以为以上几种传参方法追加参数

FastSQL也支持?占位符和可变参数：
- `varParameter(Object... vars)` 可以调用多次

**示例**

使用beanParameter方法支持传入一个参数bean
```java
@Data
public class StudentDTO{
    private String name;
    private int age;
}
```
```java
StudentDTO dto =new StudentDTO();
dto.setName="小明";
dto.setAge=10;

sqlFactory.createSQL().SELECT("*")
    .FROM("student")
    .WHERE("name=:name")
    .AND("age>:age")
    .beanParameter(dto)  //设置一个DTO查询参数
    .queryList(StudVO.class);

```
使用mapParameter方法并追加参数
```java
Map<String,Object> param = new HashMap<>();
map.put("name","李%");

sqlFactory.createSQL()
    .SELECT("*")
    .FROM("student")
    .WHERE("name").LIKE(":name")
    .AND("age > :age")
    .beanParameter(param)  //设置一个map参数
    .addParameterMapItem("age",12) //追加
    .queryList(Student.class);

```

使用varParameter方法--支持?占位符和可变参数 
```
SQL sql = sqlFactory.createSQL();
sql.INSERT_INTO("student", "id", "name", "age")
    .VALUES("?", "?", "?")
    .varParameter("123", "小明")
    .varParameter(12)
    .update();
```

##  查询方法

**查询方法解析**
- `T queryOne(Class<T> returnClassType)`查询单行结果封装为一个对象,参数可以是可以为String/Integer/Long/Short/BigDecimal/BigInteger/Float/Double/Boolean或者任意POJO的class。
- `Map<String, Object> queryMap()`查询单行结果封装为Map
- `List<T> queryList(Class<T> returnClassType)`查询多行结果封装为一个对象列表
- `List<Map<String, Object>> queryMapList()`查询多行结果封装为Map数组
- `List<Object[]> queryArrayList()` 查询结果封装为泛型为Object数组的列表
- `ResultPage<T> queryPage(int page, int perPage, Class<T> returnClassType)` 查询结果页
 

**示例**

StudentVO是查询视图类，包含name和age字段；StudentDTO是查询参数类，包含name字段。

```java
//queryList可以查询列表，可以是基本类型列表或对象列表
List<String> strings = sqlFactory.createSQL().SELECT("name")
                .FROM("student")
                .queryList(String.class); //这里执行查询列表并指定返回类型

List<StudVO> studVOList = sqlFactory.createSQL().SELECT("name", "age")
                            .FROM("student")
                            .WHERE("name=:name")
                            .beanParameter(new StudentDTO())  //设置一个DTO查询参数
                            .queryList(StudVO.class);     //查询一个对象列表

//queryOne可以查询一个值，可以是基本类型  或 对象 
String name = sqlFactory.createSQL().SELECT("name")
                 .FROM("student")
                 .WHERE("id=:id")
                 .AND("name=:name")
                 .mapItemsParameter("id", 12345) //可以传入多个k-v值，，还可以调用parameterMap传入Map参数，
                 .addParameterMapItem("name", "Jack")// 使用addParameterMapItem追加k-v值
                 .queryOne(String.class);  //这里执行查询一个对象（基本类型）并指定返回类型 
                 
StudVO studVO = sqlFactory.createSQL().SELECT("name", "age")
                   .FROM("student")
                   .WHERE("name=:name")
                   .beanParameter(new StudentDTO())  //设置一个DTO
                   .queryOne(StudVO.class);     //查询一个对象

//queryPage查询分页
ResultPage<StudVO> studVOResultPage =sqlFactory.createSQL().SELECT("name", "age")
                                        .FROM("student")
                                        .queryPage(1, 10, StudVO.class);  //分页查询（第一页，每页10条记录）
//根据特定数据库进行分页查询                    
ResultPage<StudVO> studVOResultPage =sqlFactory.createSQL().SELECT("name", "age")
                                        .FROM("student")
                                        .queryPage(1, 10, StudVO.class, DbType.MY_SQL); 
```
注意1：queryOne调用后，如果查询的值不存在是不会抛出EmptyResultDataAccessException，而是返回null，所以要用包装类型接收他的值而不是基本类型，并判断非空性

注意2：queryPage返回的是ResultPage对象


##  增删改操作：
使用update方法
```java
//插入
sqlFactory.createSQL().INSERT_INTO("student", "id", "name", "age")
        .VALUES(":id", ":name", ":age")
        .mapItemsParameter("id", 678, "name", "kjs345a354dfk", "age", 123)
        .update();
                
//修改
sqlFactory.createSQL().UPDATE("student")
        .SET("name",":name")
        .WHERE("id=678")
        .mapItemsParameter("id", 678, "name", "Rose", "age", 123)
        .update();
//删除
sqlFactory.createSQL().DELETE_FROM("student")
        .WHERE("id=:id")
        .mapItemsParameter("id", 678)
        .update();
```

##  获取数据库元信息
```java
//表名称
List<String> tableNames = sqlFactory.createSQL().getTableNames();
//列名称
List<String> columnNames = sqlFactory.createSQL().getColumnNames("student");
//列对象
List<ColumnMetaData> columnMetaDataList = sqlFactory.createSQL().getColumnMetaDataList("sys_dict");

```

##  事务管理

手动事务：FastSQL事务管理使用Spring的工具类`org.springframework.jdbc.datasource.DataSourceUtils`
```java
Connection connection = DataSourceUtils.getConnection(dataSource);//开启事务
connection.setAutoCommit(false);//关闭自动提交

sqlFactory.createSQL()
     .INSERT_INTO("sys_users", "id").VALUES(":id")
     .mapItemsParameter("id", 456)
     .update();

sqlFactory.createSQL()
    .INSERT_INTO("sys_users", "id").VALUES(":id")
    .mapItemsParameter("id", 234)
    .update();

//connection.rollback(); //回滚

connection.commit();//提交事务
```


# 6.BaseDAO

## 6.1.数据准备
### Entity实体类
注解如下 

1. @Table 非必需，如果不写表名称将会被解析为student（根据类名的下划线形式）
2. @Id 必须存在，对应表的主键
3. @Entity 非必需，标识一个实体，为了兼容JPA标准建议加上

```java
@Entity
@Table(name="student") 
@Data  //use lombok
public class Student {
    @Id
    private String id;
    private String name;
    private Integer age;
    private LocalDate birthday;
    private String homeAddress;
}

```

新建DAO层数据访问类, 并继承BaseDAO类，会自动继承BaseDAO中的方法(详见第2部分）

### DAO类在Spring环境中
DAO层：

```java
@Repository
public class StudentDAO extends BaseDAO<Student,String> {
     
}
```
在Service中：
```java
@Service
public class StudentService {
    @Autowire
    private StudentDAO studentDAO;

    @Transactional //如果需要事务--org.springframework.transaction.annotation.Transactional
    public void test1(){
        studentDAO.XXX();//调用任意方法
    }
     
}
```

### DAO类在非Spring环境中
```java
public class StudentDAO extends BaseDAO<Student,String> {
     
}
```

```java
public class Test  {
    public static void main(String[] args) {
        //构建NamedParameterJdbcTemplate
        NamedParameterJdbcTemplate namedParameterJdbcTemplate=...
        //设置
        studentDAO.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);
        //执行操作
        studentDAO.XXX();
    }
}
```
 
## 6.2.基本使用方法 CRUD 
CRUD 是四种数据操作的简称：C 表示创建，R 表示读取，U 表示更新，D 表示删除。BaseDAO 自动创建了处理数据表中数据的方法。

### 数据插入

方法 ` int insert(E entity) `，插入对象中的值到数据库，null值在生成的sql语句中会设置为NULL
```java
Student student = new Student();
student.setId(UUID.randomUUID().toString()); 
student.setName("小丽");
student.setBirthday(LocalDate.now());//这里使用jdk8时间类型
student.setHomeAddress("");

studentDao.insert(student);//获取保存成功的id

//等价如下SQL语句（注意：age被设置为null）
//INSERT INTO student(id,name,age,birthday,home_address) VALUES ('622bca40-4c64-43aa-8819-447718bdafa5','小丽',NULL,'2017-07-11','')

```

方法 ` int insertSelective(E entity)  `，插入对象中非null的值到数据库

```java
Student student = new Student();
student.setId(UUID.randomUUID().toString());
student.setName("小丽");
student.setBirthday(new Date());
student.setHomeAddress(""); 
studentDao.insertSelective(student);

//等价如下SQL语句（注意：没有对age进行保存，在数据库层面age将会保存为该表设置的默认值，如果没有设置默认值，将会被保存为null ）
//===>INSERT INTO student(id,name,birthday,home_address)  VALUES  ('622bca40-4c64-43aa-8819-447718bdafa5','小丽','2017-07-11','')
```


### 数据修改

方法   `int update(E entity) ` ,根据对象进行更新（null字段在数据库中将会被设置为null），对象中@id字段不能为空 
```java
//待更新
```

方法   `int updateSelective(E entity) `,根据对象进行更新（只更新实体中非null字段），对象中@id字段不能为空 
```java
//待更新
```

方法   `int updateByColumn(E entity, String... columns) `,根据id更新可变参数columns列，对象中@id字段不能为空 

```
Student student = studentDAO.selectOneById("11111111-1111-1111-1111-111111111111");
student.setAge(19);
studentDAO.updateByColumn(student,"age");

//===>UPDATE student SET age=? WHERE id=?
```
### 数据删除

方法 `int deleteOneById(String id) ` 根据id删除数据
```java
int num = studentDao.deleteOneById("22b66bcf-1c2e-4713-b90d-eab17182b565");//获取删除的行数量
//===>DELETE FROM student WHERE id='22b66bcf-1c2e-4713-b90d-eab17182b565'
```

方法 `int deleteAll()`,删除某个表所有行

```java
int number = studentDao.deleteAll();//获取删除的行数量
// ===>DELETE FROM student
```

方法 ` int[]  deleteInBatch(List<String> ids)` ,根据id列表批量删除数据(所有删除语句将会一次性提交到数据库)

```java
List<String> ids = new ArrayList<>();
ids.add("467641d2-e344-45e9-9e0e-fd6152f80867");
ids.add("881c80a1-8c93-4bb7-926e-9a8bc9799a72");
studentDao.deleteInBatch(ids);//返回成功删除的数量
```
方法` int deleteWhere(String sqlCondition, Object... values)`，根据条件删除
 
### 单条数据查询

方法     `E selectOneById(String id)` 
通过id查询一个对象
```java
Student student = studentDao.selectOneById("12345678");//查询id为12345678的数据，并封装到Student类中
```
方法     `E selectOneWhere(String sqlCondition, Object... values)`,通过语句查询（返回多条数据将会抛出运行时异常,为了防止sql语句在service层滥用，可变参数最多支持三个）

```java
Student student = studentDao.selectOneWhere("name=? AND home_address=?", "小明", "成都");   
  
```

方法     `protected E selectOneWhere(String sqlCondition, SqlParameterSource parameterSource)` 查询一条数据，protected，只能在子类中使用
 
### 多条数据查询

方法     `List<E> selectWhere(String sqlCondition, Object... values)`,用法与selectOneWhere()相同，可以返回一条或多条数据，可变参数最多支持三个

```java
List<Student> studentList  =  studentDao.selectWhere("name=?", "小明");
List<Student> studentList  =  studentDao.selectWhere("ORDER BY age");
List<Student> studentList  =  studentDao.selectWhere("home_address IS NULL ORDER BY age DESC");
//...
```

方法     `List<E> selectAll()` 查询所有
```java
List<Student> allStudents  =  studentDao.selectAll();
```
方法     `protected List<E> selectWhere(String sqlCondition, SqlParameterSource parameterSource)`可以返回一条或多条数据， protected，只能在子类中使用

### 分页查询

方法     `ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, Object... values)` 

方法     `protected ResultPage<E> selectPageWhere(String sqlCondition, int pageNumber, int perPage, SqlParameterSource parameterSource)` ， protected，只能在子类中使用

方法     `ResultPage<E> selectPage(int pageNumber, int perPage)` 

### 其他查询
方法     `int countWhere(String sqlCondition, Object... values)`,通过条件查询数量

```java
int countWhere = studentDao.countWhere("age >= 20"); //查找年龄大于等于20的学生
int countWhere = studentDao.countWhere("age > ?" , 10); //查找年龄大于10的学生
```

方法     `protected int countWhere(String sqlCondition, SqlParameterSource parameterSource)`,通过条件查询数量， protected，只能在子类中使用
```java
@Repository
public class BizPhotoDAO extends ApplicationBaseDAO<BizPhotoPO, String> {
    public int countByName() {
        return countWhere("photo_name=:name", new MapSqlParameterSource().addValue("name", "物品照片"));
    }
}
```

方法   `int count()` 查询表总数量

##  定制你的ApplicationBaseDAO
建议在你的程序中实现ApplicationBaseDAO，可以

1. 定制一些通用方法
2. 改变BaseDAO的默认属性
3. 设置多数据库支持
4. 设置BaseDAO中的触发器

```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
  //添加方法等
}

////我们的StudentDAO此时应该继承ApplicationBaseDAO
@Repository
public class StudentDAO extends ApplicationBaseDAO<Student,String> {
     
}
```
###  定制通用方法
如下，增加了一个名为logicDelete的逻辑删除方法，将会作用于继承于它的每个DAO
```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
  //...其他方法
  
  public void logicDelete(ID id) {
      //每个表都有一个defunct，1表示已（逻辑）删除
      namedParameterJdbcTemplate.getJdbcOperations().update("UPDATE " + this.tableName + " SET defunct = 1");
  }
  
  //...其他方法
}
```
上面的logicDelete方法使用了tableName这个变量，BaseDAO中的部分可用变量为
```
Class<E> entityClass; //DAO对应的实体类
Class<ID> idClass;  //标识为@Id的主键类型

Logger log; //日志，可以在实现类中直接使用

String className; //实体类名
String tableName; //表名
 
Field idField;  //@Id对应的字段引用
String idColumnName; //表主键列名

namedParameterJdbcTemplate //jdbc模板

//...待更新

```

### 改变BaseDAO的默认属性
```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
    public ApplicationBaseDAO() {
        //BaseDAO中方法（比如selectWhere等）允许最大的可变参数最大个数
        this.variableParameterLimit = 3;
        
        //修改默认的类型，1.集成这个类的子类分页方法使用 2.this.SELECT 分页使用
        this.dataSourceType = DbType.MY_SQL; 
    }
}
```

### 设置多数据源支持
```java
public abstract class OracleApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
      //重写setNamedParameterJdbcTemplate方法
      @Autowired
      @Qualifier("oracleNamedParameterJdbcTemplate")//===>根据名称注入
      @Override
      protected void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
          super.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);
          dataSourceType
 dataSourceType
}
public abstract class MySqlApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
      //重写setNamedParameterJdbcTemplate方法
      @Autowired
      @Qualifier("mysqlNamedParameterJdbcTemplate")//===>根据名称注入
      @Override
      protected void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
          super.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);
          dataSourceType
      }}
}dataSourceType### 设置BaseDAO中的拦截器

```java
public abstract class ApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
    public ApplicationBaseDAO() {
         // 1.设置触发器开关
         this.useBeforeInsert = true; //在插入前执行
         this.useBeforeUpdate = true; //在更新前执行
    }

    //2.重写触发器相关方法
    @Override
    protected void beforeInsert(E object) {
        EntityRefelectUtils.setFieldValue(object, idField, UUID.randomUUID().toString());
        EntityRefelectUtils.setFieldValue(object, "createdAt", LocalDateTime.now());
        EntityRefelectUtils.setFieldValue(object, "updatedAt", LocalDateTime.now());//在插入数据时自动更新id,createdAt,updatedAt
    }

    @Override
    protected void beforeUpdate(E object) {
        EntityRefelectUtils.setFieldValue(object, "updatedAt", LocalDateTime.now());//在更新数据时自动更新updatedAt
    }
}
```
对应关系如下:

count 参数表示执行成功的条数 

| 启用            | 需重写的方法                      | 作用于                                                        |
| :-------------- | :-------------------------------- | :------------------------------------------------------------ |
| useBeforeInsert | beforeInsert(E entity)            | insertSelective(..)/insert(..)执行插入之前                    |
| useAfterInsert  | afterInsert(E entity,int count)   | insertSelective(..)/insert(..)执行插入之后                    |
| useBeforeUpdate | beforeUpdate(E entity)            | updateSelective(..)/update(..)执行更新之前                    |
| useAfterUpdate  | afterUpdate(E entity,int count)   | updateSelective(..)/update(..)/updateByColumn(..)执行更新之后 |
| useBeforeDelete | beforeDelete(ID id)               | deleteOneById(..)执行删除之前                                 |
| useAfterDelete  | void afterDelete(ID id,int count) | deleteOneById(..)执行删除之后                                 |

##   SQL构建器在BaseDAO中的使用
BaseDAO整合了SQL构建器，在继承BaseDAO的类中你可以你可以直接调用 `this.SELECT(..)/this.UPDATE(..) /this.DELETE(..) /this.INSERT(..)` , 注意：不用设置 **namedParameterJdbcTemplate**或者**dataSource**

```java
@Repository
public class StudentDAO extends ApplicationBaseDAO<Student, String> {
    public void queryListByName() {
        List<Student> list = this.SELECT("*").FROM(this.tableName)
                                        .WHERE("name").LIKE("'李%'")
                                        .queryList(Student.class);//查询列表
    }
    public void updateById() {
        this.UPDATE(this.tableName).SET("name","Jakk").WHERE("id").eq("'22222222-2222-2222-2222-222222222222'").update();
    }
}
```

#  通用工具

## 获取sql的IN列表

```
 
`FastSQLUtils.getInClause(Collection<?> collection) `,会根据Collection的类型自动判断使用什么样的分隔符:

```java
FastSQLUtils.getInClause(Lists.newArrayList(1, 23, 4, 15))  //生成=>(1,23,4,15)
FastSQLUtils.getInClause(Lists.newArrayList("dog", "people", "food", "apple")) //生成=> ('dog','people','food','apple')
```

说明：IN功能已经整合到SQL构建器的IN方法


#  配置项 
显示sql日志
```properties
#显示sql
logging.level.org.springframework.jdbc.core.JdbcTemplate=debug
#显示绑定的参数
logging.level.org.springframework.jdbc.core.StatementCreatorUtils=trace
```
