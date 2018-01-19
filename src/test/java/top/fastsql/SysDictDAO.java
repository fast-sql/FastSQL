package top.fastsql;

import top.fastsql.dao.BaseDAO;

public class SysDictDAO extends BaseDAO<SysDict, String> {

    public SysDictDAO(SQLFactory sqlFactory) {
        super(sqlFactory);
    }
}
