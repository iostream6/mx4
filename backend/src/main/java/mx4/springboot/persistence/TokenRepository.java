/*
 * 2020.05.06  -- Created
 */
package mx4.springboot.persistence;

import java.util.Optional;
import mx4.springboot.security.utils.TokenManager.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This interface provided automagicked token persistence. Only blacklisted access tokens as well as valid refresh tokens are persisted
 *
 * @author Ilamah, Osho
 */
public interface TokenRepository extends MongoRepository<Token, String> {
   
    Optional<Token> findByValueAndType(String value, String type);
    
    Optional<Token> findByKey(String key);
    
}
