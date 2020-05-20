/*
 * 2020.04.03 - Created
 * 2020.05.17 - Validated user level  broker update/delete endpoints
 * 2020.05.18 - Revised 'update' endpoint, no longer throws exception | uses ResponseEntity and send the right status codes/message bodies
 */
package mx4.springboot.services;

import java.util.List;
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

    @PostMapping("/api/brokers") //Might also use ResponseEntity return type so we can set location. See Gutierez pp 101 & https://stackoverflow.com/a/51916728
    public ResponseEntity<?> create(@RequestBody Broker b) {
        if (b.getName() == null || b.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            brokerRepository.save(b);
            return ResponseEntity.ok(b);
        }
    }

    @GetMapping("/api/brokers/{uid}")
    public List<Broker> readUserBrokers(@PathVariable String uid) {
        return brokerRepository.findByUserId(uid);
    }

    @PutMapping("/api/broker/{uid}")
    public ResponseEntity<?> update(@RequestBody Broker b, @PathVariable String uid) {
//        return brokerRepository.findByUserIdAndId(uid, b.getId()).map(entity -> {
//            entity.setName(b.getName());
//            return brokerRepository.save(entity);
//        }).orElseThrow(() -> new ItemNotFoundException(b.getId(), TYPE));
        if (b.getId() == null || b.getName() == null || b.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
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

    @DeleteMapping("/api/broker/{uid}/{bid}") //RequestBody not widely supported and doesnt even make much sense for DELETE requests   //https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request AND   //https://github.com/axios/axios/issues/897
    public void delete(@PathVariable String uid, @PathVariable String bid) {
        brokerRepository.findByUserIdAndId(uid, bid).ifPresent(broker -> {
            brokerRepository.delete(broker);
        });
    }

// TODO implement the Admin level endpoints
}
