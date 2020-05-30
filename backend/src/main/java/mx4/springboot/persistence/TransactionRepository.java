/*
 * 2020.05.24  - Created
 */
package mx4.springboot.persistence;

import java.util.List;
import mx4.springboot.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    public List<Transaction> findByPortfolioId(String portfolioId);
    
    public void deleteByPortfolioId(String portfolioId);
    
}
