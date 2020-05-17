/*
 * 2020.04.03  - Created
 */
package mx4.springboot.persistence;

import java.util.List;
import java.util.Optional;
import mx4.springboot.model.Broker;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface BrokerRepository extends MongoRepository<Broker, String> {

    public List<Broker> findByUserId(String userId);

    public Optional<Broker> findByUserIdAndId(String userId, String Id);

}
