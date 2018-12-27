package top.fastsql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author ChenJiazhi
 */
@Entity
@Table(name = "test")
public class TestEntity {
    @Id
    private String user ;

    private String ordId;

    private Integer depId;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOrdId() {
        return ordId;
    }

    public void setOrdId(String ordId) {
        this.ordId = ordId;
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "user='" + user + '\'' +
                ", ordId='" + ordId + '\'' +
                ", depId=" + depId +
                '}';
    }
}
