# 1.简介
FastSQL一个基于spring-jdbc的简单ORM框架，它支持SQL构建、SQL执行、自动映射和通用DAO。结合了Hibernate/JPA快速开发和Mybatis高效执行的优点。

FastSQL可以完全满足你控制欲，可以用Java代码清晰又方便写出SQL并执行。 


# 2.入门

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

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件中：
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
因此 SQLFactory 的最佳作用域是应用作用域。有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。

**SQL**

SQL 实例是有状态的 ，也不是线程安全的，因此是不能被共享的。绝对不能将 SQL 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 
每执行SQL语句一次，都需要构建一个 SQL 实例，返回一个结果。

# 3.SQLFactory 配置


# 4.SQL类作为sql语句构建器使用
Java程序员面对的最痛苦的事情之一就是在Java代码中嵌入SQL语句。FastSQL提供`cn.com.zdht.pavilion.FastSQL.SQL`类和`cn.com.zdht.pavilion.FastSQL.SQL`类简化你的构建过程。



## 4.1.数据准备 

在mysql数据库中新建表：

student(学生表)，score（成绩表）,一个学生可以有多个成绩

```sql
CREATE TABLE `student` (
  `id` varchar(36) NOT NULL,
  `name` varchar(10) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `home_address` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
)
```

```sql
CREATE TABLE `score` (
  `id` varchar(36) NOT NULL,
  `student_id` varchar(36) NOT NULL,
  `subject_name` varchar(255) NOT NULL,
  `score_value` int(11) NOT NULL,
   PRIMARY KEY (`id`)
)
```
插入数据
```sql
INSERT INTO `student` VALUES ('00000000-0000-0000-0000-000000000000', '小明', '13', '2004-06-15', '成都市高新区');
INSERT INTO `student` VALUES ('11111111-1111-1111-1111-111111111111', '丁丁', '12', '2005-11-24', '成都市锦江区');
INSERT INTO `student` VALUES ('22222222-2222-2222-2222-222222222222', '小红', '9', '2008-05-12', '上海市虹桥区');

INSERT INTO `score` VALUES ('1a7ceefe-0785-4efb-8a52-25ab7936fa41', '00000000-0000-0000-0000-000000000000', '数学', '99');
INSERT INTO `score` VALUES ('b5702d1e-1638-4013-ace5-0bb4a74eebb5', '00000000-0000-0000-0000-000000000000', '语文', '59');
INSERT INTO `score` VALUES ('97ce41ac-b08b-4b46-8b46-e162d3ae0b3b', '11111111-1111-1111-1111-111111111111', '数学', '70');
INSERT INTO `score` VALUES ('37d0d684-95df-40a6-a9ed-d2b97aa951fc', '11111111-1111-1111-1111-111111111111', '语文', '80');
```


## 4.2.构建查询语句

### 生成SQL字符串

- build() 生成SQL
- toString() 生成SQL
- buildAndPrintSQL() 生成并打印SQL

### 基本查询
SELECT方法可以传入一个可变参数，以便选择多列。(FastSQL中建议SQL关键字全部采用大写)
```java
new SQL().SELECT("name", "age").FROM("student").WHERE("age>10").build();
//==> SELECT name,age FROM student WHERE age>10
new SQL().SELECT("name", "age").FROM("student").WHERE("name='小红'").build();
//==> SELECT name,age FROM student WHERE name='小红'
```
单独提供了WHERE()关键字方法，生成WHERE 1=1,如下
```java
SQL sql = SQL.SELECT("name", "age").FROM("student").WHERE();
if (true){//判断
  sql.AND("age > 10");
}
if (false){//判断
  sql.AND("age < 8");
}

//===>SELECT name,age  FROM student  WHERE 1 = 1  AND age > 10 
```
### 使用操作符方法
FastSQL提供了一些操作符方便SQL的构建，比如：

```java
new SQL().SELECT("name", "age").FROM("student").WHERE("age").lt("10").AND("name").eq("'小明'").build();
//==> SELECT name,age FROM student WHERE age > 10 AND name = '小明'
```

如下：

| 方法               | 说明                                   |
| :----------------- | :------------------------------------- |
| eq(String value)   | 生成=，是equals的缩写                  |
| gt(String value)   | 生成>，是greater than的缩写            |
| gtEq(String value) | 生成>=，是greater than or equals的缩写 |
| lt(String value)   | 生成<，是less than的缩写               |
| ltEq(String value) | 生成<=，是less than or equals的缩写    |
| nEq(String value)  | 生成!=，是not equals的缩写             |

注意：
`eq("1")`生成` = 1` ，`eq("'1'")`会生成` = '1'`

### 使用LIKE
```java
new SQL().SELECT("name", "age").FROM("student").WHERE("name").LIKE("'王%'").build();
//==> SELECT name,age FROM student WHERE name LIKE '王%'
```

### 使用连接查询/排序

查询不及格的成绩

```java
String sql = new SQL().SELECT("s.name","c.subject_name","c.score_value")
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
### 分组查询
查询每个学生总分数
```java
String sql =new SQL().SELECT("s.name", "sum(c.score_value) total_score")
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

### IN语句  
FastSQL支持几种IN语句拼写方式：
```java
//1.使用字符串
new SQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN("('小明','小红')")
   .build();
new SQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN$_$("'小明','小红'")//IN$_$ 生成sql: IN (...)
   .build();
//2.使用集合（List,Set等）
new SQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN(Lists.newArrayList("小明","小红"))
   .build();
//3.IN_var 使用可变参数 ，仅支持int和String类型的可变参数
new SQL().SELECT("*")
   .FROM("student")
   .WHERE("name").IN_var("小明","小红")//
   .build();

//输出===>SELECT *  FROM student  WHERE name  IN ('小明','小红')
```
### 子查询subQuery
查询大于平均分的成绩（可以使用subQuery()方法/$_$()方法）
```java
new SQL().SELECT("*")
   .FROM("score")
   .WHERE("score_value >")
   .subQuery(
         SQL.SELECT("avg(score_value)").FROM("score")
    )
   .build();

new SQL().SELECT("*")
   .FROM("score")
   .WHERE("score_value >")
   .$_$(
         SQL.SELECT("avg(score_value)").FROM("score")
    )
   .build();
//SELECT *  FROM score  
//WHERE score_value >  ( SELECT avg(score_value)  FROM score  )
```
带有IN的子查询
```java
new SQL().SELECT("*")
    .FROM("score")
    .WHERE()
    .AND("score")
    .IN$_$(
         SQL.SELECT("DISTINCT score_value").FROM("score")
    )
    .build();
//SELECT * FROM score WHERE 1 = 1 AND score IN (SELECT DISTINCT score_value FROM score)
```

### AND和OR结合使用
如果查询年龄大于10岁，并且名字是小明或小红

错误的写法：
```java
new SQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND("name='小明' OR name='小红'")
   .build();

//SELECT *  FROM student  WHERE age>10  AND name='小明' OR name='小红'
//OR条件少了括号
```
正确的写法：
```java
new SQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND("(name='小明' OR name='小红')")//手动添加括号
   .build();

new SQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND$_$("name='小明' OR name='小红'")//AND$_$生成 AND (...)
   .build();

new SQL().SELECT("*")
   .FROM("student")
   .WHERE("age>10")
   .AND().$_$("name='小明' OR name='小红'")//$_$ 生成左右括号
   .build();
```

### 使用Lambda表达式简化判断语句
- `ifTrue(boolean bool, Consumer<SQL> sqlConsumer)`:如果第1个参数为true，则执行第二个参数（Lambda表达式）
- `ifNotEmpty(Collection<?> collection, Consumer<SQL> sqlConsumer)`:如果第1个参数长度大于0，则执行第二个参数（Lambda表达式）
- `ifPresent(Object object, Consumer<SQL> sqlConsumer)`:如果第1个参数存在（不等于null且不为""），则执行第二个参数（Lambda表达式）

```java
new SQL().SELECT("student")
    .WHERE("id=:id")
    .ifTrue(true, thisBuilder -> thisBuilder.AND("name=:name"))  //使用
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
##  4.3.构建插入语句
使用 INSERT_INTO 和 VALUES
```java
//使用列
new SQL().INSERT_INTO("student", "id", "name", "age")
                .VALUES(":id", ":name", ":age").build();
//=>INSERT INTO student (id,name,age)  VALUES (:id,:name,:age)

//不使用列
new SQL().INSERT_INTO("student").VALUES(":id", ":name", ":age").build();
//=>INSERT INTO student VALUES (:id,:name,:age)
```

##  4.4.构建更新语句

SET(String column, String value) :SET关键字

setOne(String column, String value) :追加一个值


```java
new SQL().UPDATE("student").SET("name","'Jack'").setOne("age","9").WHERE("name").eq("'Mike'").build();
//=>  UPDATE student SET name='Jack',age=9 WHERE name = 'Mike'              
```

##  4.5.构建删除语句
```java
new SQL().DELETE_FROM("student").WHERE("id=:id").build();
//=>DELETE FROM student WHERE id=:id                
```

## 4.6.分页功能
**使用原生关键字进行分页**
```java
new SQL().SELECT("*").FROM("student").LIMIT(10).buildAndPrintSQL();
new SQL().SELECT("*").FROM("student").LIMIT(5, 10).buildAndPrintSQL();
new SQL().SELECT("*").FROM("student").LIMIT(10).OFFSET(5).buildAndPrintSQL();
```
生成如下SQL
```sql
SELECT * FROM student LIMIT 10
SELECT * FROM student LIMIT 5,10
SELECT * FROM student LIMIT 10 OFFSET 5
```

**分页方法进行分页**
```
//row方法第一个参数是第几页(从1开始)，第二个参数表示每页几条（从1开始），第三个参数可选，为数据库类型

SQL.SELECT("*").FROM("student").rows(1,10).buildAndPrintSQL();//根据配置项来决定使用哪一种数据库分页方法
SQL.SELECT("*").FROM("student").databaseType(DbType.POSTGRSQL).rows(2,5).buildAndPrintSQL();//postgresql分页
SQL.SELECT("*").FROM("student").databaseType(DbType.ORACLE).rows(2,5).buildAndPrintSQL();//oracle
SQL.SELECT("*").FROM("student").databaseType(DbType.MY_SQL).rows(2,5).buildAndPrintSQL();//mysql分页
```
注意：如果不指定 databaseType，将会使用 FastSQLConfig#databaseType 的默认类型进行分页;

**获取数量语句**
```java
new SQL().SELECT("*").FROM("student").count().buildAndPrintSQL();
```

## 4.7.更多关键字


| 方法                                         | 示例                   | 说明                    |
| :------------------------------------------- | :--------------------- | :---------------------- |
| append(String)                               |                        | 追加任意字符串          |
| subQuery(SQL)                         |                        | 子查询 使用括号包裹参数 |
| subQuery(String)                             |                        | 子查询 使用括号包裹参数 |
| nl()                                         |                        | 回车换行                |
| SELECT(String... columns)                    | SQL.SELECT("a","b")    | 查询列                  |
| appendSELECT(String...)                      | .appendSELECT("c","d") | 追加查询列              |
| FROM(String table)                           | .FROM("student")       | FROM                    |
| DELETE_FROM(String table)                    |                        | 删除                    |
| INSERT_INTO(String table, String... columns) |                        | 插入                    |
| VALUES(String... columnValues)               |                        |                         |
| UPDATE(String table)                         |                        |                         |
| LEFT_JOIN_ON(String table, String on)        |                        |                         |
| RIGHT_JOIN_ON(String table, String on)       |                        |                         |
| LEFT_JOIN(String table  )                    |                        |                         |
| RIGHT_JOIN(String table  )                   |                        |                         |
| ON(String on)                                |                        |                         |
| WHERE()                                      |                        | 生成WHERE 1=1           |
| WHERE(String condition)                      |                        |                         |
| AND(String condition)                        |                        |                         |
| OR(String condition)                         |                        |                         |
| ORDER_BY(String condition)                   |                        |                         |
| GROUP_BY(String condition)                   |                        |                         |
| comma()                                      |                        | 添加逗号                |
| $_()                                         |                        | 左括号(                 |
| _$()                                         |                        | 右括号)                 |
| $_$(SQL)                              |                        | 使用括号包裹参数        |
| AS(String value)                             |                        |                         |
| ASC()                                        |                        |                         |
| DESC()                                       |                        |                         |
| IN()                                         |                        |                         |
| IN(String sql)                               |                        |                         |
| IN(Collection<?> collection)                 |                        |                         |
| LIKE(String value)                           |                        |                         |
| IS_NULL()                                    |                        |                         |
| IS_NOT_NULL()                                |                        |                         |
| LIMIT(Integer offset, Integer rows)          |                        |                         |
| LIMIT(Integer rows)                          |                        |                         |
| OFFSET(Integer offset)                       |                        |                         |
| UNION()                                      |                        |                         |
| UNION_ALL()                                  |                        |                         |
| HAVING(String condition)                     |                        |                         |


# 5.SQL构建器的执行功能

SQL构建器支持SpringJDBC的NamedParameterJdbcTemplate类，并简化了它的API的使用：



## 5.1.设置查询模板
```java
//创建任意DataSource对象（这里使用了spring自带的数据源SimpleDriverDataSource）
DataSource dataSource = new SimpleDriverDataSource(
                new Driver(), "jdbc:postgresql://192.168.0.226:5432/picasso_dev2?stringtype=unspecified", 
                "developer", "password");

//创建NamedParameterJdbcTemplate
NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

```
查询模板可通过template(),dataSource()等方法设置
- `template(NamedParameterJdbcTemplate template)`方法传入namedParameterJdbcTemplate，内部使用这个对象进行查询
- `dataSource(DataSource dataSource)`传入dataSource,内部会自动生成namedParameterJdbcTemplate

```java
//使用NamedParameterJdbcTemplate对象
new SQL().SELECT("name").FROM("student")
    .template(namedParameterJdbcTemplate)//这里传入namedParameterJdbcTemplate
    .queryList(String.class); 

//使用DataSource对象,内部会自动生成NamedParameterJdbcTemplate对象
new SQL().SELECT("name", "age")
    .FROM("student")
    .dataSource(dataSource)//这里传入dataSource 
    .queryList(StudVO.class);     
```

##  5.2.设置参数
FastSQL支持多种传入命名参数的方法：

- `parameter(SqlParameterSource sqlParameterSource)` 支持传入SqlParameterSource类型的参数（兼容spring-jdbc）
-  ~~`parameterDTO(Object dto)`方法可以传入对象参数~~(请使用beanParameter代替)
- `beanParameter(Object dto)`方法可以传入对象参数
-  ~~`parameterMap(Map<String, Object> map)`支持传入Map类型参数~~（请使用mapParameter）
- `mapParameter(Map<String, Object> map)`支持传入Map类型参数
- ~~`parameterMapItems(Object... param)`~~ (请使用mapItemsParameter代替)
- `mapItemsParameter(Object... param)`支持多个key-value形式的参数，比如`mapItemsParameter("id", 12345,"name","小明")`
- `beanAndMapParameter(Object dto, Map<String, Object> map)` 支持两种不同的参数组合
- `addParameterMapItem(String key, Object value)`可以为以上几种传参方法追加参数

FastSQL也支持?占位符和可变参数：
- `varParameter(Object... vars)` 可以调用多次

**示例**

使用beanParameter方法支持传入一个参数bean
```java
@Data
public class StudentDTO{
    private String name;
}
```
```java
StudentDTO dto =new StudentDTO();
dto.setName="小明";

new SQL().SELECT("name", "age")
    .FROM("student")
    .WHERE("name=:name")
    .beanParameter(dto)  //设置一个DTO查询参数
    .dataSource(dataSource)
    .queryList(StudVO.class);

```
使用mapParameter方法并追加参数
>撰写中

使用varParameter方法--支持?占位符和可变参数 
```
String id = UUID.randomUUID().toString();
SQL.INSERT_INTO("student", "id", "name", "age")
    .VALUES("?", "?", "?")
    .template(namedParameterJdbcTemplate)
    .varParameter(id, "小明")
    .varParameter(12)
    .update();
```

##  5.3.基本查询：

**查询方法解析**
- `T queryOne(Class<T> returnClassType)`查询单行结果封装为一个对象,参数可以是可以为String/Integer/Long/Short/BigDecimal/BigInteger/Float/Double/Boolean或者任意POJO的class。
- `Map<String, Object> queryMap()`查询单行结果封装为Map
- `List<T> queryList(Class<T> returnClassType)`查询多行结果封装为一个对象列表
- `List<Map<String, Object>> queryMapList()`查询多行结果封装为Map数组
- `List<Object[]> queryArrayList()`查询结果封装为泛型为Object数组的列表
- `ResultPage<T> queryPage(int page, int perPage, Class<T> returnClassType)`查询结果页

**查询并打印结果**

- `queryMapListAndPrint()` 
- `queryArrayListAndPrint` 

**示例**

StudVO是查询视图类，包含name和age字段；StudentDTO是查询参数类，包含name字段。

```java
//queryList可以查询列表，可以是基本类型列表或对象列表
List<String> strings = new SQL().SELECT("name")
                .FROM("student")
                .template(namedParameterJdbcTemplate)
                .queryList(String.class); //这里执行查询列表并指定返回类型

List<StudVO> studVOList = new SQL().SELECT("name", "age")
                            .FROM("student")
                            .WHERE("name=:name")
                            .beanParameter(new StudentDTO())  //设置一个DTO查询参数
                            .dataSource(dataSource)
                            .queryList(StudVO.class);     //查询一个对象列表

//queryOne可以查询一个值，可以是基本类型  或 对象 
String name = new SQL().SELECT("name")
                 .FROM("student")
                 .WHERE("id=:id")
                 .AND("name=:name")
                 .dataSource(dataSource)
                 .mapItemsParameter("id", 12345) //可以传入多个k-v值，，还可以调用parameterMap传入Map参数，
                 .addParameterMapItem("name", "Jack")// 使用addParameterMapItem追加k-v值
                 .queryOne(String.class);  //这里执行查询一个对象（基本类型）并指定返回类型 
                 
StudVO studVO = new SQL().SELECT("name", "age")
                   .FROM("student")
                   .WHERE("name=:name")
                   .beanParameter(new StudentDTO())  //设置一个DTO
                   .dataSource(dataSource)
                   .queryOne(StudVO.class);     //查询一个对象

//queryPage查询分页
ResultPage<StudVO> studVOResultPage =new SQL().SELECT("name", "age")
                                        .FROM("student")
                                        .dataSource(dataSource)
                                        .queryPage(1, 10, StudVO.class);  //分页查询（第一页，每页10条记录）
//根据特定数据库进行分页查询                    
ResultPage<StudVO> studVOResultPage =new SQL().SELECT("name", "age")
                                        .FROM("student")
                                        .dataSource(dataSource)
                                        .queryPage(1, 10, StudVO.class, DbType.MY_SQL); 
```
注意1：queryOne调用后，如果查询的值不存在是不会抛出EmptyResultDataAccessException，而是返回null，所以要用包装类型接收他的值而不是基本类型，并判断非空性

注意2：queryPage返回的是ResultPage对象


## 5.4.增删改操作：
使用update方法
```java
//插入
new SQL().INSERT_INTO("student", "id", "name", "age")
        .VALUES(":id", ":name", ":age")
        .template(namedParameterJdbcTemplate)
        .mapItemsParameter("id", 678, "name", "kjs345a354dfk", "age", 123)
        .update();
                
//修改
new SQL().UPDATE("student")
        .SET("name",":name")
        .WHERE("id=678")
        .template(namedParameterJdbcTemplate)
        .mapItemsParameter("id", 678, "name", "Rose", "age", 123)
        .update();
//删除
new SQL().DELETE_FROM("student")
        .WHERE("id=:id")
        .template(namedParameterJdbcTemplate)
        .mapItemsParameter("id", 678)
        .update();
```

## 5.5.获取数据库元信息
```java
//表名称
List<String> tableNames = new SQL().dataSource(dataSource).getTableNames();
//列名称
List<String> columnNames = new SQL().dataSource(dataSource).getColumnNames("student");
//列对象
List<ColumnMetaData> columnMetaDataList = new SQL().dataSource(dataSource).getColumnMetaDataList("sys_dict");

```

## 5.6.事务管理

手动事务：FastSQL事务管理使用Spring的工具类`org.springframework.jdbc.datasource.DataSourceUtils`
```java
Connection connection = DataSourceUtils.getConnection(dataSource);//开启事务
connection.setAutoCommit(false);//关闭自动提交

new SQL().INSERT_INTO("sys_users", "id").VALUES(":id")
     .mapItemsParameter("id", UUID.randomUUID().toString())
     .dataSource(dataSource).update();

new SQL().INSERT_INTO("sys_users", "id").VALUES(":id")
    .mapItemsParameter("id", UUID.randomUUID().toString() + UUID.randomUUID().toString())
    .dataSource(dataSource).update();

//connection.rollback(); //回滚

connection.commit();//提交事务
```

在Spring环境中，可以直接使用注解@Transactional控制事务

 

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
        this.databaseType = DbType.MY_SQL; 
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
          databaseType
 databaseType
}
public abstract class MySqlApplicationBaseDAO<E, ID> extends BaseDAO<E, ID> {
      //重写setNamedParameterJdbcTemplate方法
      @Autowired
      @Qualifier("mysqlNamedParameterJdbcTemplate")//===>根据名称注入
      @Override
      protected void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
          super.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate);
          databaseType
      }}
}databaseType### 设置BaseDAO中的拦截器

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
