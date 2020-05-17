/*
 * 2020.04.08  - Created
 */
package mx4.springboot.persistence;


import mx4.springboot.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

    User findByUserCode(String usercode);

    User findByEmail(String email);

}
