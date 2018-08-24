package top.fastsql.dto;

import org.springframework.jdbc.support.KeyHolder;

/**
 * @author ChenJiazhi
 */
public class KeyHolderResult {

    private Integer count;
    private KeyHolder keyHolder;

    public KeyHolderResult(Integer count, KeyHolder keyHolder) {
        this.count = count;
        this.keyHolder = keyHolder;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public KeyHolder getKeyHolder() {
        return keyHolder;
    }

    public void setKeyHolder(KeyHolder keyHolder) {
        this.keyHolder = keyHolder;
    }
}
