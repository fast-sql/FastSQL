# 一.FastSql简介
一个基于spring-jdbc的简单ORM框架，结合了hibernate和mybatis的优点,
主要使用了NamedParameterJdbcTemplate类，可以加速你的数据库开发。
注意：
1.主键名称必须为id（暂时）
 

# 二.BaseDAO
应用中数据访问类需要继承这个类，进行各种操作

## 1.准备数据
mysql新建表，student和city是多对一关系。
```
CREATE TABLE `student` (
  `id` varchar(36) NOT NULL,
  `name` varchar(10) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `home_address` varchar(255) DEFAULT NULL,
  `city_id` varchar(36) NOT NULL,
   PRIMARY KEY (`id`)
)

CREATE TABLE `city` (
  `id` varchar(36) NOT NULL,
  `name` varchar(20) NOT NULL,
   PRIMARY KEY (`id`)
)
```
新建java实体类，（同名或驼峰下转划线相同可以省略@Table）。
```
@Table(name="student") 
public class Student {
    private String id;
    private String name;
    private Integer age;
    private LocalDate birthday;
    private String homeAddress;
    private String cityId;
    //省略getter和setter
}

@Table(name="") 
public class City {
    private String id;
    private String name;
    //省略getter和setter
}

```
新建DAO层数据访问类, 并继承BaseDAO类，会自动继承BaseDAO中的方法(详见第2部分）

```
@Repository
public class StudentDAO extends BaseDAO<Student,String> {
     
}

@Repository
public class CityDAO extends BaseDAO<City,String> {
     
}
```

## 2.数据保存的方法 ，继承自BaseDAO

####   `  int save(E entity) `
插入对象中的值到数据库，null值在数据库中会设置为NULL
```
Student student = new Student();
//student.setId(UUID.randomUUID().toString()); 
student.setName("小丽");
student.setBirthday(new Date());
student.setHomeAddress("");

studentDao.save(student);//获取保存成功的id
```
等价如下SQL语句（注意：age被设置为null）
```
INSERT INTO student(id,name,age,birthday,home_address,city_id) 
 VALUES 
('622bca40-4c64-43aa-8819-447718bdafa5','小丽',NULL,'2017-07-11','',NULL)
```


####   `  int saveIgnoreNull(E entity)  `
插入对象中非null的值到数据库
```
Student student = new Student();
student.setId(UUID.randomUUID().toString());
student.setName("小丽");
student.setBirthday(new Date());
student.setHomeAddress("");
 
studentDao.saveIgnoreNull(student);
```
等价如下SQL语句（注意：没有对age进行保存，在数据库层面age将会保存为该表设置的默认值，如果没有设置默认值，将会被保存为null ）
```
INSERT INTO student(id,name,birthday,home_address) 
 VALUES 
('622bca40-4c64-43aa-8819-447718bdafa5','小丽','2017-07-11','')

```
## 3.数据删除的方法 ，继承自BaseDAO

####   `int delete(String id) `
根据id删除数据
```
int num = studentDao.delete("22b66bcf-1c2e-4713-b90d-eab17182b565");//获取删除的行数量
```
等价如下SQL语句
```
DELETE FROM student WHERE id='22b66bcf-1c2e-4713-b90d-eab17182b565'
```

####   `int deleteAll()`
删除某个表所有行
```
int number = studentDao.deleteAll();//获取删除的行数量
```

####  ` int  deleteInBatch(List<String> ids)` 和 `public int deleteInBatch(String... ids)`
根据id列表批量删除数据(所有删除语句将会一次性提交到数据库)
```
List<String> ids = new ArrayList<>();
ids.add("467641d2-e344-45e9-9e0e-fd6152f80867");
ids.add("881c80a1-8c93-4bb7-926e-9a8bc9799a72");
int number = studentDao.deleteInBatch(ids);//返回成功删除的数量
```

## 4.数据修改的方法 ，继承自BaseDAO中

#### `String update(E entity) `
根据对象进行更新（null字段在数据库中将会被设置为null），对象中id字段不能为空 

#### `String updateIgnoreNull(E entity) `
根据对象进行更新（只更新实体中非null字段），对象中id字段不能为空 

 
## 5.单表查询,使用findXXX，继承自BaseDAO中的方法

### 5.1 单个对象
####   `E findOne(String id)` 
通过id查询一个对象
```
Student student = studentDao.findOne("12345678");//查询id为12345678的数据，并封装到Student类中
```
####   `E findOneWhere(String sqlCondition, Object... values)`
通过语句查询（返回多条数据将会抛出运行时异常）
```
Student student = studentDao.findOneWhere("name=?1 AND home_address=?2", "小明", "成都");   
  
```
### 5.2 多个对象
小明将会被匹配到?1中，成都将会被匹配到?2中，查询的是名字的小明，家庭地址为成都的对象。

####   `List<E> findListWhere(String sqlCondition, Object... values)`
用法与findOneWhere()相同，可以返回一条或多条数据
```
List<Student> studentList  =  studentDao.findListWhere("name=?1", "小明");
List<Student> studentList  =  studentDao.findListWhere("ORDER BY age");
List<Student> studentList  =  studentDao.findListWhere("home_address IS NULL ORDER BY age DESC");
//...
```

####   `List<E> findListWhere(String sqlCondition, BeanPropertySqlParameterSource parameterSource)`
```
class StudentIndexDTO{
    private String name;
    private LocalDate birthday;
    //getter setter
}

StudentIndexDTO dto = new StudentIndexDTO();
dto.setName("%小%");
dto.setBirthday(LocalDate.of(1991,10,10));

List<Student> studentList = studentDao.findListWhere(
                "name LIKE :name AND  birthday < :birthday ",
                new BeanPropertySqlParameterSource(dto));
);
```
####   `List<E> findListWhere(String sqlCondition, Map<String, Object> parameterMap)`
使用Map作为命名参数
```
Map<String, Object> map = new HashMap<>();
map.put("name", "%小%");
 
List<Student> studentList = studentDao.findListWhere(
      "name LIKE :name  ORDER BY age DESC" , map
);
```
### 5.3 统计对象
####   `int countWhere(String sqlCondition, Object... values)`
通过条件查询数量
```
int countWhere = studentDao.countWhere("age >= 20"); //查找年龄大于等于20的学生
int countWhere = studentDao.countWhere("age > ?1" , 10); //查找年龄大于10的学生

```
# 四.通过sql查询（通过BaseDao中的queryXXX方法）

## 把结果封装到Map中
以下方法可以获取Map对象（由column和值组成的）
```
Map<String, Object> queryMapBySql(String sql)
Map<String, Object> queryMapBySql(String sql, SqlParameterSource paramSource) 
Map<String, Object> queryMapBySql(String sql, Map<String, ?> paramMap)
```


以下方法获取Map对象的列表（每个map由column和值组成的）
```
List<Map<String, Object>> queryMapListBySql(String sql)
List<Map<String, Object>> queryMapListBySql(String sql, SqlParameterSource paramSource) 
List<Map<String, Object>> queryMapListBySql(String sql, Map<String, ?> paramMap)
```
 

## 把结果封装到对象中

新建数据传入类StudentIndexDTO
```
public class StudentIndexDTO {
    private Integer age;
    private String cityName;
    //getter.setter
}
```
新建数据展示对象StudentVO
```
public class StudentVO extends Student {
    private String cityName;

    //getter.setter
}

```
在StudentDAO中增加方法
```
public class StudentDAO extends BaseDAO<Student> {

    public List<StudentVO> findStudentVOList(StudentIndexDTO dto) {
        String sql = "SELECT s.*,c.name AS cityName FROM student s " +//template可以直接使用
                "LEFT JOIN city c ON s.city_id = c.id " +
                "WHERE s.age = :age AND c.name = :cityName ";//命名参数

        List<StudentVO> studentVOList = template.query(
                sql,//命名参数
                new BeanPropertySqlParameterSource(dto), //传入参数***
                new BeanPropertyRowMapper<>(StudentVO.class));//匹配传出参数***
        return studentVOList;
    }
}
```
# 四.SQLBuilder--SQL构建器
Java程序员面对的最痛苦的事情之一就是在Java代码中嵌入SQL语句。提供SQLBuilder简化你的构建过程。
## DEMO 1
```
String sql_1 = new SQLBuilder()
        .SELECT("name", "age")
        .FROM("student")
        .WHERE("age>10")
        .build();
```
生成如下SQL
```
SELECT name,age
FROM student
WHERE age>10
```
## DEMO 2
```
String city = "成都";
String sql_2 = new SQLBuilder()
      .SELECT("s.name", "s.age")
      .FROM("student s")
      .LEFT_JOIN_ON("city c", "c.id=s.id")
      .WHERE("s.age>10")
      .IF_PRESENT_AND(city, "city.name LIKE :city")//如果把city改为null或者"" 这句话将不会添加
      .build();
```
生成如下SQL
```
SELECT s.name,s.age
   FROM student s
   LEFT OUTER JOIN city c ON ( c.id=s.id ) 
   WHERE s.age>10  AND city.name LIKE :city 
```
## DEMO 3
```
String sql_3 = new SQLBuilder()
      .SELECT("s.name", "s.age")
      .FROM("student s")
      .LEFT_JOIN_ON("city c", "c.id=s.id")
      .WHERE()
      .AND("(age>10 OR age<5)")
      .ORDER_BY("s.age")
      .build();
```
生成如下SQL
```
SELECT s.name,s.age
FROM student s
LEFT OUTER JOIN city c ON ( c.id=s.id ) 
WHERE 1=1   AND (age>10 OR age<5)  ORDER BY s.age 
```
# 五.分页工具PageSqlUtils和分页查询
分页工具支持mysql/postgresql/oracle

public static String findSQL(String sql, int pageNumber, int perPageSize) 会返回查找行sql

public static String countSQL(String sql) 会返回查找数量sql

以mysql为例
```
String baseSql = new SQLBuilder()
        .SELECT("name", "age")
        .FROM("student")
        .WHERE("age>10")
        .build();
        
String rowsSql = PageSqlUtils.getRowsSQL(baseSql,1,10);   //生成=> SELECT name,age FROM student WHERE age>10 LIMIT 0,10

String numberSql = PageSqlUtils.getNumberSQL(baseSql);       //生成=> SELECT count(*) ( SELECT name,age   FROM student WHERE age>10 )
```
分页API
```
DbPageResult<E> queryPageBySql(String baseSql, int pageNumber, int perPage, Map<String, ?> paramMap)
DbPageResult<E> queryPageBySql(String baseSql, int pageNumber, int perPage, BeanPropertySqlParameterSource parameterSource)
```
示例
```
public class StudentDAO extends BaseDAO<Student,String> {
     /**
      * 查询前十条数据
      */
     public DbPageResult<StudentVO> findStudentVOPage(StudentIndexDTO dto) {
         String sql = "SELECT s.*,c.name AS cityName FROM student s " +//template可以直接使用
                 "LEFT JOIN city c ON s.city_id = c.id " +
                 "WHERE s.age = :age AND c.name = :cityName ";//命名参数
 
         DbPageResult<StudentVO> studentDbPageResult = queryPageBySql(
                 sql, 1, 10,  //sql ,页数 ，和每页条数
                 new BeanPropertySqlParameterSource(dto), //匹配传入参数 
                 new BeanPropertyRowMapper<>(StudentVO.class));//匹配传出参数 
         return studentDbPageResult;
     }
}
```
 
 
# 六.配置项 

1.配置数据库类型 可选mysql postgresql oracle
```
fastsql.db-type=mysql  
```
2.显示sql日志
```
logging.level.org.springframework.jdbc.core.JdbcTemplate=debug
logging.level.org.springframework.jdbc.core.StatementCreatorUtils=trace
```

