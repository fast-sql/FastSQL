package top.fastsql.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chenjiazhi
 * 2018-04-05
 */
public class RowMap extends HashMap<String, Object> {

    public RowMap() {
    }

    public RowMap(Map<String, Object> map) {
        super(map);
    }

    /**
     * get Type
     */
    public Class<?> getType(String key) {
        return get(key).getClass();
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Integer getInteger(String key) {
        return (Integer) get(key);
    }


    public Date getDate(String key) {
        return (Date) get(key);
    }

    @SuppressWarnings("unchecked")
    public <CLS> CLS getValue(String key, Class<CLS> cls) {
        return (CLS) get(key);
    }


}
