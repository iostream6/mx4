/*
 * 2020.04.03 - Created
 * 2020.05.17 - Validated user level  broker update/delete endpoints
 */
package mx4.springboot.services;

import java.util.List;
import mx4.springboot.persistence.BrokerRepository;
import mx4.springboot.model.Broker;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String TYPE = "Broker";

    @Autowired
    private BrokerRepository brokerRepository;

    @PostMapping("/api/brokers") //Might also use ResponseEntity return type so we can set location. See Gutierez pp 101 & https://stackoverflow.com/a/51916728
    public Broker create(@RequestBody Broker e) {
        return brokerRepository.save(e);
    }

    @GetMapping("/api/brokers/{uid}")
    public List<Broker> readUserBrokers(@PathVariable String uid) {
        return brokerRepository.findByUserId(uid);
    }

    @PutMapping("/api/broker/{uid}")
    public Broker update(@RequestBody Broker b, @PathVariable String uid) {
        return brokerRepository.findByUserIdAndId(uid, b.getId()).map(entity -> {
            entity.setName(b.getName());
            return brokerRepository.save(entity);
        }).orElseThrow(() -> new ItemNotFoundException(b.getId(), TYPE));
    }

    @DeleteMapping("/api/broker/{uid}/{bid}") //RequestBody not widely supported?   //https://stackoverflow.com/questions/299628/is-an-entity-body-allowed-for-an-http-delete-request AND   //https://github.com/axios/axios/issues/897
    public void delete(@PathVariable String uid, @PathVariable String bid) {
        brokerRepository.findByUserIdAndId(uid, bid).ifPresent(broker -> {
            brokerRepository.delete(broker);
        });
    }

//    @GetMapping("/admin/brokers")
//    public List<Broker> readAll() {
//        return brokerRepository.findAll();
//    }
//
//    @GetMapping("/brokers/{id}")
//    public Broker readOne(@PathVariable String id) {
//        final Optional<Broker> optional = brokerRepository.findById(id);
//        return optional.orElseThrow(() -> new ItemNotFoundException(id, TYPE));  //will be caught and customized by (@ControllerAdvice) class ItemNotFoundAdvicer
//    }
//
////    @PutMapping("/brokers/{id}")
////    public Broker update(@RequestBody Broker e, @PathVariable String id) {
////        return brokerRepository.findById(id).map(entity -> {
////            entity.setName(e.getName());
////            entity.setCode(e.getCode());
////            return brokerRepository.save(entity);
////        }).orElseGet(() -> {
////            e.setId(id);
////            return brokerRepository.save(e);
////        });
////    }
//    @DeleteMapping("/brokers/{id}")
//    public void delete(@PathVariable String id) {
//        brokerRepository.deleteById(id);
//    }
}
