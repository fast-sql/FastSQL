# 一.FastSql简介
一个基于spring-jdbc的简单ORM框架，主要使用了NamedParameterJdbcTemplate类，可以加速你的数据库开发。

## 为什么使用SQL而不是ORM框架

1.
2.
3.

## 数据库设计约定：

1.使用uuid字符串做为主键类型
2. 数据库表名称最好使用单数
3.
 
# 二.BaseDAO
应用中数据访问类需要继承这个类，进行各种操作

## 1.准备数据
新建表
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
新建java实体类，同名或驼峰下转划线相同可以省略@TableName
```
@TableName("student") 
public class Student {
    private String id;
    private String name;
    private Integer age;
    private Date birthday;
    private String homeAddress;
    private String cityId;
    //省略getter和setter
}

public class City {
    private String id;
    private String name;
    //省略getter和setter
}

```
新建DAO层数据访问类, 并继承BaseDAO类，会自动集成BaseDAO中的方法---见第2部分

```
@Repository
public class StudentDAO extends BaseDAO<Student> {
     
}

@Repository
public class CityDAO extends BaseDAO<City> {
     
}
```

## 2.数据保存 ，继承自BaseDAO中的方法

#### public String save(E entity) 
插入对象中的值到数据库，null值在数据库中会设置为NULL
```
Student student = new Student();
//student.setId(UUID.randomUUID().toString()); //不指定id将会自动把id保存为uuid
student.setName("小丽");
student.setBirthday(new Date());
student.setHomeAddress("");

String id = studentDao.save(student);//获取保存成功的id
```
等价如下SQL语句（注意：age被设置为null）
```
INSERT INTO student(id,name,age,birthday,home_address) 
 VALUES 
('622bca40-4c64-43aa-8819-447718bdafa5','小丽',NULL,'2017-07-11','')
```


#### public String saveIgnoreNull(E entity)  
插入对象中非null的值到数据库
```
Student student = new Student();
//student.setId(UUID.randomUUID().toString());//不指定id将会自动把id保存为uuid
student.setName("小丽");
student.setBirthday(new Date());
student.setHomeAddress("");
 
String id =  studentDao.saveIgnoreNull(student);//获取保存成功的id
```
等价如下SQL语句（注意：没有对age进行保存，在数据库层面age将会保存为该表设置的默认值，如果没有设置默认值，将会被保存为null ）
```
INSERT INTO student(id,name,birthday,home_address) 
 VALUES 
('622bca40-4c64-43aa-8819-447718bdafa5','小丽','2017-07-11','')

```
## 3.数据删除 ，继承自BaseDAO中的方法

#### public int delete(String id) 
根据id删除数据
```
int deleteRowNumber = studentDao.delete("22b66bcf-1c2e-4713-b90d-eab17182b565");
```
等价如下SQL语句
```
DELETE FROM student WHERE id='22b66bcf-1c2e-4713-b90d-eab17182b565'
```

#### public int deleteAll()
删除某个表所有行
```
int number = studentDao.deleteAll()//获取删除的行数量
```

#### public int  deleteInBatch(List<String> ids) 和 public int deleteInBatch(String... ids)
根据id列表批量删除数据(所有删除语句将会一次性提交到数据库)
```
List<String> ids = new ArrayList<>();
ids.add("467641d2-e344-45e9-9e0e-fd6152f80867");
ids.add("881c80a1-8c93-4bb7-926e-9a8bc9799a72");
int number = studentDao.deleteInBatch(ids);//返回成功删除的数量
```

## 4.数据修改 ，继承自BaseDAO中的方法

#### String update(E entity) 
根据对象进行更新（null字段在数据库中将会被设置为null），对象中id字段不能为空 

#### String updateIgnoreNull(E entity) 
根据对象进行更新（只更新实体中非null字段），对象中id字段不能为空 

#### String update(String id, Map<String, Object> updateColumnMap) 
使用id根据map进行更新
```
Map<String, Object> map = new HashMap<>();
map.put("home_address", "成都");// map.put("homeAddress", "成都") -- 使用实体字段作为key也可以
map.put("birthday", new Date());
map.put("age", null);

studentDao.update("12345678", map);

```
等价如下SQL语句
```
UPDATE student 
SET home_address='成都', birthday='2017-07-17',age=NULL 
WHERE id='12345678'
```
## 5.单表查询，继承自BaseDAO中的方法

### 5.1 单个对象
#### public E findOne(String id) 
通过id查询一个对象
```
Student student = studentDao.findOne("12345678");//查询id为12345678的数据，并封装到Student类中
```
#### public E findOneWhere(String sqlCondition, Object... values)
通过语句查询（返回多条数据将会抛出运行时异常）
```
Student student = studentDao.findOneWhere("name=?1 AND home_address=?2", "小明", "成都");
```
### 5.2 多个对象
小明将会被匹配到?1中，成都将会被匹配到?2中，查询的是名字的小明，家庭地址为成都的对象。

#### public List<E> findListWhere(String sqlCondition, Object... values)
用法与findOneWhere()相同，可以返回一条或多条数据
```
List<Student> studentList  =  studentDao.findListWhere(
                        "name LIKE ?1 OR home_address IS NULL ORDER BY age DESC", "%明%");
```

#### public List<E> findListWhere(String sqlCondition, BeanPropertySqlParameterSource parameterSource)
```
Student student = new Student();
student.setName("%小%");
student.setBirthday(new Date());

List<Student> studentList1 = studentDao.findListWhere(
                "name LIKE :name AND  ( birthday < :birthday OR home_address IS NULL)",
                new BeanPropertySqlParameterSource(student)
);
```
#### public List<E> findListWhere(String sqlCondition, Map<String, Object> parameterMap)
使用Map作为命名参数
```
Map<String, Object> map = new HashMap<>();
map.put("name", "%小%");
map.put("birthday", new Date());
 

List<Student> studentList1 = studentDao.findListWhere(
      "name LIKE :name OR birthday < :birthday ORDER BY age DESC" , map
);
```
### 5.3 统计对象
#### public int countWhere(String sqlCondition, Object... values)
通过条件查询数量
```
int countWhere = studentDao.countWhere("age >= 20"); //查找年龄大于等于20的学生
int countWhere = studentDao.countWhere("age > ?1" , 10); //查找年龄大于10的学生

```
## 5.通过sql(关联)查询（通过BaseDao中template对象）
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

        List<StudentVO> studentVOList = template.query(sql,//命名参数
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
WHERE s.age>10
AND city.name LIKE :city 
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
WHERE 1=1 
AND (age>10 OR age<5)
ORDER BY s.age 
```
# 五.分页工具PageSqlUtils
