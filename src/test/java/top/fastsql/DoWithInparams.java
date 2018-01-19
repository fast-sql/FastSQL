//package top.fastsql;
//
//import com.google.common.collect.Lists;
//import org.junit.Test;
//import top.fastsql.util.FastSqlUtils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//public class DoWithInparams {
//
//    @Test
//    public void test() {
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000_0000; i++) {
//            String s = doWith(
//                    "SELECT id FROM place_filings WHERE id=? AND state IN ? AND gender_id IN ? AND name LIKE ?",
//                    "id-0909-87", Lists.newArrayList(202, 201), Lists.newArrayList("1", "2"), "%大酒店"
//            );
//        }
//        System.out.println((System.currentTimeMillis() - start) / 1000d);
//
//    }
//
//    @Test
//    public void test2() {
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000_0000; i++) {
//            String s = doWith2(
//                    "SELECT id FROM place_filings WHERE id=? AND state IN ? AND gender_id IN ? AND name LIKE ?",
//                    "id-0909-87", Lists.newArrayList(202, 201), Lists.newArrayList("1", "2"), "%大酒店"
//            );
//        }
//        System.out.println((System.currentTimeMillis() - start) / 1000d);
//
//    }
//
//
//    /**
//     * 使用param数组中的Collection变量来替换相应索引位置的 sql中的？,生成符合条件的sql
//     * <br>
//     * 如：
//     * <pre>
//     *  doWith(
//     *      "SELECT id FROM place_filings WHERE id=? AND state IN ? AND gender_id IN ? AND name LIKE ?",
//     *      "id-0909-87",Lists.newArrayLists(202,201),Lists.newArrayLists("1","2"),"%大酒店"
//     * )；
//     * </pre>
//     * 生成：
//     * <pre>
//     *     SELECT id FROM place_filings WHERE id=? AND state IN (202,201)
//     *            AND gender_id IN ('1','2') AND name LIKE ?
//     * </pre>
//     */
//    private String doWith(String sql, Object... params) {
//        List<Integer> indexList = new ArrayList<>();
//        int i = 0;
//        for (Object param : params) {
//            if (param instanceof Collection) {
//                indexList.add(i);
//            }
//            i++;
//        }
//
//        //记录每个？的索引
//        List<Integer> wenhaoIndexs = new ArrayList<>();
//
//        String[] split = sql.split("");
//        int i2 = 0;
//        for (String s : split) {
//            if (s.equals("?")) {
//                wenhaoIndexs.add(i2);
//            }
//            i2++;
//        }
//
//        for (Integer integer : indexList) {
//            //取得这个？索引
//            Integer index = wenhaoIndexs.get(integer);
//            split[index] = FastSqlUtils.getInClause((Collection<?>) params[integer]);
//        }
//        return String.join("", split);
//    }
//
//
//    private String doWith2(String sql, Object... params) {
//        List<Integer> indexList = new ArrayList<>();
//        int i = 0;
//        for (Object param : params) {
//            if (param instanceof Collection) {
//                indexList.add(i);
//            }
//            i++;
//        }
//
//
//        //使用 ？ 分割 sql
//        String[] split = sql.split("\\?");
//
//        // 使用 ？ 连接各部分 生成 list
//        List<String> sqls = new ArrayList<>();
//
//        int i2 = 0;
//        for (String s : split) {
//            if (i2 != 0) {
//                sqls.add("?");
//            }
//            sqls.add(s);
//            i2++;
//        }
//        if (sql.trim().endsWith("?")) { //特殊特殊情况处理
//            sqls.add("?");
//        }
//
//
//        // 替换list部分相应的？
//        for (Integer index : indexList) {
//            sqls.set(index * 2 + 1, FastSqlUtils.getInClause((Collection<?>) params[index]));
//        }
//
//        return String.join("", sqls);
//    }
//}
