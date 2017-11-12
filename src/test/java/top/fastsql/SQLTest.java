package top.fastsql;

import org.junit.Test;

public class SQLTest {

    private static SQLFactory sqlFactory = new SQLFactory();

    @Test
    public void testOperatorMethod(){

    }

    @Test
    public void testByType(){
        sqlFactory.createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("age").lt().byType(10)
                .AND("name").eq().byType("小明")
                .build();
    }


}
