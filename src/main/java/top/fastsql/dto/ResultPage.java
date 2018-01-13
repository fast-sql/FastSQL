package top.fastsql.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 陈佳志
 * 2017-08-15
 */
public class ResultPage<T> implements Iterable<T> {
    /**
     * 内容
     */
    private List<T> content;
    /**
     * 总长度
     */
    private int totalElements; //TODO 考虑改为long?

    public ResultPage() {
        this.content = new ArrayList<>();
        this.totalElements = 0;
    }

    public ResultPage(List<T> content, int totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public ResultPage(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = (int) totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }


    @Override
    public String toString() {
        return "ResultPage{" + "totalElements=" + totalElements + ", content=" + content + '}';
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}

