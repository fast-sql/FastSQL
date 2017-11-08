package org.fastsql.dto;

/**
 * 批量修改的结果
 *
 * @author 陈佳志
 * 2017-10-30
 */
public class BatchUpdateResult {

    private int[] affectRows;
    private int affectRowNumber;

    public BatchUpdateResult(int[] affectRows) {
        this.affectRows = affectRows;
        int update = 0;
        for (int i : affectRows) {
            update = update + i;
        }
        this.affectRowNumber = update;
    }

    public int[] getAffectRows() {
        return affectRows;
    }

    public void setAffectRows(int[] affectRows) {
        this.affectRows = affectRows;
    }

    public int getAffectRowNumber() {
        return affectRowNumber;
    }

    public void setAffectRowNumber(int affectRowNumber) {
        this.affectRowNumber = affectRowNumber;
    }
}
