/*
 * 2020.03.31  - Created
 */
package mx4.springboot.persistence;

import java.util.List;
import java.util.Optional;
import mx4.springboot.model.Currency.Exchange;
import org.springframework.data.mongodb.repository.MongoRepository;
//{"success":true,"timestamp":1585904948,"base":"EUR","date":"2020-04-03","rates":{"USD":1.079436,"GBP":0.879541,"NGN":396.153627}}

/**
 *
 * @author Ilamah, Osho
 */
//https://stackoverflow.com/questions/30880927/spring-boot-extending-crudrepository
public interface ExchangeRepository extends MongoRepository<Exchange, String> {

    public Optional<Exchange> findByFromIdAndToId(long fromId, long toId);

    public List<Exchange> findByFromId(long fromId);

}
