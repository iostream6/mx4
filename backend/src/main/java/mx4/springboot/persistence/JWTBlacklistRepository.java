/*
 * 2020.05.06  -- Created
 */
package mx4.springboot.persistence;

import java.util.List;
import mx4.springboot.security.utils.TokenManager;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This interface provided automagicked blacklist token persistence
 *
 * @author Ilamah, Osho
 */
public interface JWTBlacklistRepository extends MongoRepository<TokenManager.Blacklist, String> {

    List<TokenManager.Blacklist> findByToken(String token);
}
