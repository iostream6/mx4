/*
 * 2020.03.31  - Created
 */
package mx4.springboot.persistence;

import java.util.List;
import java.util.Optional;
import mx4.springboot.model.Portfolio.PortfolioUser;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface PortfolioUserRepository extends MongoRepository<PortfolioUser, String> {

//    public void deleteByPortfolioIDAndUserId(String portfolioID, String userId);

    public void deleteByPortfolioID(String portfolioID);
    
    public Optional<PortfolioUser> findByUserIdAndPortfolioID(String userId, String portfolioID);
//
    public List<PortfolioUser> findByUserId(String userId);
//
    public List<PortfolioUser> findByPortfolioID(String portfolioID);


}
