/*
 * 2020.06.17 - Created
 */
package mx4.springboot.persistence;

import java.util.Date;
import java.util.Optional;
import mx4.springboot.model.Values;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface ValuesRepository extends MongoRepository<Values, String> {
    public Optional<Values> findByDate(Date date);
}
