/*
 * 2020.09.19  - Created
 */
package mx4.springboot.persistence;

import mx4.springboot.model.DatedQuotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Ilamah, Osho
 */
//https://stackoverflow.com/questions/10067169/query-with-sort-and-limit-in-spring-repository-interface
//https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#reference
//https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#repositories.core-concepts
//https://www.baeldung.com/queries-in-spring-data-mongodb
public interface QuotesRepository extends MongoRepository<DatedQuotes, String> {
    //FindByX  List<User> users = userRepository.findByName("Eric");
    
    //StartingWith and endingWith     List<User> findByNameStartingWith(String regexp);
    
    // Between     List<User> findByAgeBetween(int ageGT, int ageLT);
    
    //Like and OrderBy    List<User> users = userRepository.findByNameLikeOrderByAgeAsc("A");

    @Override
    public Page<DatedQuotes> findAll(Pageable pageable);
}
