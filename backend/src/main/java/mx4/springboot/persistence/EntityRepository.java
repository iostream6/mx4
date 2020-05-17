/*
 * 2020.03.31  - Created
 */
package mx4.springboot.persistence;

import mx4.springboot.model.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface EntityRepository extends MongoRepository<Entity, String> {
}
