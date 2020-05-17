/*
 * 2020.03.31  - Created
 */
package mx4.springboot.persistence;

import java.util.List;
import mx4.springboot.model.Portfolio.PortfolioUser;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface PortfolioUserRepository extends MongoRepository<PortfolioUser, String> {

    public void deleteByPortfolioIDAndUserCode(String portfolioID, String userCode);

    public List<PortfolioUser> findByUserCode(String userCode);

    public List<PortfolioUser> findByPortfolioID(String portfolioID);


}
