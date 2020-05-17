/*
 * 2020.03.31  - Created
 */
package mx4.springboot.model;

/**
 *
 * @author Ilamah, Osho
 */
public class Broker {

    private String name = "";//make sure always non-null

    private String id = "";//make sure always non-null

    private String userId; //make sure always non-null

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
