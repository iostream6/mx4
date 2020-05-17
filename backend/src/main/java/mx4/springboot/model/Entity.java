/*
 * 2020.03.31  - Created
 */
package mx4.springboot.model;

/**
 * An entity, whose instruments we may hold. Entities may be a company with one or more stock instruments, a government - with one or more bond instruments, etc.
 *
 * @author Ilamah, Osho
 */
public class Entity {

    private String name;
    
    private String description;

    private String id;

    public Entity(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Entity() {
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
