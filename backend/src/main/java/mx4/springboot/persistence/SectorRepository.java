/*
 * 2020.05.24  - Created
 */
package mx4.springboot.persistence;

import mx4.springboot.model.Sector;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface SectorRepository extends MongoRepository<Sector, String> {
}
