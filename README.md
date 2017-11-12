# 1.简介
FastSQL一个基于spring-jdbc的简单ORM框架，它支持sql构建、sql执行、查询结果自动映射和通用DAO。结合了Hibernate/JPA快速开发和Mybatis高效执行的优点。

FastSQL可以完全满足你控制欲，可以用Java代码清晰又方便地写出sql语句并执行。 


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

# 3.SQLFactory 配置


# 4.SQL类作为sql语句构建器使用
Java程序员面对的最痛苦的事情之一就是在Java代码中嵌入SQL语句。FastSQL提供`SQL`类简化你构建sql语句的过程。
```
SQLFactory sqlFactory = new SQLFactory();
```


## 1).查询select语句

### 基本查询
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
### 使用操作符方法
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

| 方法         | 说明                                  |
| :----------- | :----------------------------------- |
| eq(String)   | 生成=，并追加参数（equals的缩写）                  |
| gt(String)   | 生成>，并追加参数（是greater than的缩写）            |
| gtEq(String) | 生成>=，并追加参数（是greater than or equals的缩写） |
| lt(String)   | 生成<，并追加参数（是less than的缩写 ）              |
| ltEq(String) | 生成<=，并追加参数（是less than or equals的缩写）    |
| nEq(String)  | 生成!=，并追加参数（是not equals的缩写  ）           |
| LIKE(String)  | 生成LIKE 并追加参数，                  |
| NOT_LIKE(String)  | 生成NOT LIKE ,并追加参数           |
| NOT_LIKE(String)  | 生成NOT LIKE ，并追加参数          |
| IS_NULL()         | 生成IS NULL                |
| IS_NOT_NULL()     | 生成IS NOT NULL            |

注意：
- 这些方法仅仅是字符串连接：`eq("1")`生成` = 1` ，`eq("'1'")`会生成` = '1'`。
- 同样提供无参数方法 eq()/gt()/... 不会追加参数。

### 操作符方法 结合 byType(Object)方法：

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
### 分组查询
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

### IN语句  
FastSQL支持几种IN语句拼写方式：
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
### 子查询subQuery
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

### AND和OR结合使用
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

### 使用Lambda表达式简化构建动态sql
- `ifTrue(boolean bool, Consumer<SQL> sqlConsumer)`:如果第1个参数为true，则执行第二个参数（Lambda表达式）
- `ifNotEmpty(Collection<?> collection, Consumer<SQL> sqlConsumer)`:如果第1个参数长度大于0，则执行第二个参数（Lambda表达式）
- `ifPresent(Object object, Consumer<SQL> sqlConsumer)`:如果第1个参数存在（不等于null且不为""），则执行第二个参数（Lambda表达式）

```java
sqlFactory.createSQL().SELECT("student")
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


##  2).构建插入/修改、删除语句
使用 INSERT_INTO 和 VALUES

```java
//使用列
sqlFactory.createSQL().INSERT_INTO("student", "id", "name", "age")
                .VALUES(":id", ":name", ":age").build();
//=>INSERT INTO student (id,name,age)  VALUES (:id,:name,:age)

//不使用列
sqlFactory.createSQL().INSERT_INTO("student").VALUES(":id", ":name", ":age").build();
//=>INSERT INTO student VALUES (:id,:name,:age)
```

SET(String column, String value) :SET关键字

setOne(String column, String value) :追加一个值


```java
sqlFactory.createSQL().UPDATE("student").SET("name","'Jack'").setOne("age","9").WHERE("name").eq("'Mike'").build();
//=>  UPDATE student SET name='Jack',age=9 WHERE name = 'Mike'              
```

构建删除语句

```java
sqlFactory.createSQL().DELETE_FROM("student").WHERE("id=:id").build();
//=>DELETE FROM student WHERE id=:id                
```
 
#  /////////////////////////////////未完待续///////////////////////////////