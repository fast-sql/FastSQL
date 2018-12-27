package top.fastsql;

/**
 * @author ChenJiazhi
 */

public class TestEntityMain {
    public static void main(String[] args) {
        SQLFactory sqlFactory = SQLFactory.createUseSimpleDateSource(
                "jdbc:mysql://192.168.146.146:3306/test?relaxAutoCommit=true", "root", "locroot"
        );

        TestEntityDAO testEntityDAO = new TestEntityDAO();
        testEntityDAO.setSqlFactory(sqlFactory);

        TestEntity entity = new TestEntity();
        entity.setDepId(1);
        entity.setOrdId("5");

        System.out.println(testEntityDAO.selectOneByEntity(entity));

        TestEntity entity2 = new TestEntity();
        entity2.setDepId(1);
        System.out.println(testEntityDAO.selectByEntity(entity2));
    }
}
