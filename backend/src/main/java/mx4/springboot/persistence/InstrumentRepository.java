/*
 * 2020.05.24  - Created
 */
package mx4.springboot.persistence;

import java.util.List;
import mx4.springboot.model.Instrument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface InstrumentRepository extends MongoRepository<Instrument, String> {

    public List<Instrument> findByCode(String code);
}
