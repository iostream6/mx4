/*
 * 2020.05.25  - Created
 */
package mx4.springboot.persistence;

import mx4.springboot.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface TransactionsRepository extends MongoRepository<Transaction, String>  {
    
}
