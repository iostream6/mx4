/*
 * 2020.03.31  - Created
 */
package mx4.springboot.persistence;

import java.util.Optional;
import mx4.springboot.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {

    public Optional<Portfolio> findByCode(String code);

    public Optional<Portfolio> findByBrokerId(String brokerId);

}
