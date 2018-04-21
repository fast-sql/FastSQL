package top.fastsql;

import org.junit.Test;
import top.fastsql.util.FastSqlUtils;

/**
 * @author Jiazhi
 * @since 2017/11/15
 */
public class FastSQLUtilsTest {

    @Test
    public void test(){
        FastSqlUtils.bothWildcard("李");
        FastSqlUtils.leftWildcard("李");
        FastSqlUtils.rightWildcard("李");
    }
    @Test
    public void in(){
        System.out.println(
                FastSqlUtils.getInClause(
                        "state in {} and id IN {}", FastSqlUtils.listOf(1, 2, 3), FastSqlUtils.listOf("11", "qwq"))
        );

    }
}
