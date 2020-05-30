/*
 * 2020.04.03 - Created
 * 2020.05.17 - Validated user level  broker update/delete endpoints
 * 2020.05.18 - Revised 'update' endpoint, no longer throws exception | uses ResponseEntity and send the right status codes/message bodies
 * 2020.05.30 - Added some minor implementation changes plus javadocs
 */
package mx4.springboot.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx4.springboot.persistence.BrokerRepository;
import mx4.springboot.model.Broker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ilamah, Osho
 */
@RestController
public class BrokerService {

    @Autowired
    private BrokerRepository brokerRepository;

    /**
     * Creates a new Broker record in the backing data store, provided a broker with the same name does not already exist.
     *
     * @param b the Broker model
     * @return if successful, the created broker model,
     */
    @PostMapping("/api/brokers")
    public ResponseEntity<?> createBroker(@RequestBody Broker b) {
        if (b.getName() != null || b.getUserId() != null) {
            //
            final Optional<Broker> existingBrokerOptional = brokerRepository.findByUserIdAndName(b.getUserId(), b.getName());
            if (existingBrokerOptional.isPresent()) {
                Map<String, Object> responseData = new HashMap();
                responseData.put("portfolio-error", "Portfolio name already in use");
                responseData.put("success", false);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(responseData);
            } else {
                brokerRepository.save(b);
                return ResponseEntity.ok(b);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    /**
     * Gets a list of brokers associated with a particular user.
     *
     * @param uid the user id
     * @return the list of brokers
     */
    @GetMapping("/api/brokers/{uid}")
    public List<Broker> readUserBrokers(@PathVariable String uid) {
        return brokerRepository.findByUserId(uid);
    }

    /**
     * Updates an existing broker record
     *
     * @param b the broker model
     * @param uid the user id
     * @return the updated broker model
     */
    @PutMapping("/api/broker/{uid}")
    public ResponseEntity<?> update(@RequestBody Broker b, @PathVariable String uid) {
        if (b.getName() != null || b.getUserId() != null) {
            //
            final Optional<Broker> brokerOptional = brokerRepository.findByUserIdAndId(uid, b.getId());
            if (brokerOptional.isPresent()) {
                final Broker broker = brokerOptional.get();
                broker.setName(b.getName());
                brokerRepository.save(broker);
                return ResponseEntity.ok(broker);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @DeleteMapping("/api/broker/{uid}/{bid}")
    public void delete(@PathVariable String uid, @PathVariable String bid) {
        // to safely delete a broker, we must first delete all portfolio's associated with that broker, 
        //which in turn requires us to delete all transactions associated with the portfolios
        // we expect that this logic is enfoced client side - so client when requested to delete broker first deletes all transactions, 
        //then all portfolios, then broker (aborting at any point if an error occurs
        brokerRepository.findByUserIdAndId(uid, bid).ifPresent(broker -> {
            brokerRepository.delete(broker);
        });
    }

// TODO implement the Admin level endpoints
}
