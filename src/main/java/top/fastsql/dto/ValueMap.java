//package top.fastsql.dto;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Chenjiazhi
// * 2018-03-29
// * @see RowMap
// */
//@Deprecated
//public class ValueMap extends HashMap<String, Object> {
//
//    public ValueMap() {
//    }
//
//    public ValueMap(Map<String,Object> map) {
//
//        super(map);
//    }
//
//    public String getString(String key) {
//        return (String) get(key);
//    }
//
//    /**
//     * get Type
//     */
//    public Class<?> getType(String key) {
//        return  get(key).getClass();
//    }
//
//    public Integer getInteger(String key) {
//        return (Integer) get(key);
//    }
//
////    public LocalDateTime getLocalDateTime(String key) {
////        Object data = get(key);
////        if (data instanceof Date) {
////            return toLocalDateTime((Date) data);
////        } else {
////            return (LocalDateTime) data;
////        }
////    }
//
////    public LocalDate getLocalDate(String key) {
////        Object data = get(key);
////        if (data instanceof Date) {
////            return toLocalDate((Date) data);
////        } else {
////            return (LocalDate) data;
////        }
////    }
//
//    public  Date getDate(String key) {
//        return (Date) get(key);
//    }
//
//    @SuppressWarnings("unchecked")
//    public <CLS> CLS getValue(String key, Class<CLS> cls) {
//        return (CLS) get(key);
//    }
//
//
//}
