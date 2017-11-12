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
sqlFactory.createSQL().SELECT("name", "age").FROM("student").WHERE("age").lt("10").AND("name").eq("'小明'").build();
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

### `byType(Object)`方法 ：


 
/////////////////////////////////未完待续///////////////////////////////