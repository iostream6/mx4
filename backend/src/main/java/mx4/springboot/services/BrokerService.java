/*
 * 2020.04.03 - Created
 */
package mx4.springboot.services;

import java.util.List;
import java.util.Optional;
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

    @PutMapping("/api/broker/{uid}/{bid}")
    public Broker update(@RequestBody Broker e, @PathVariable String uid, @PathVariable String bid) {
        return brokerRepository.findByUserIdAndId(uid, bid).map(entity -> {
            entity.setName(e.getName());
            return brokerRepository.save(entity);
        }).orElseThrow(() -> new ItemNotFoundException(bid, TYPE));
    }  
    






    @GetMapping("/admin/brokers")
    public List<Broker> readAll() {
        return brokerRepository.findAll();
    }

    @GetMapping("/brokers/{id}")
    public Broker readOne(@PathVariable String id) {
        final Optional<Broker> optional = brokerRepository.findById(id);
        return optional.orElseThrow(() -> new ItemNotFoundException(id, TYPE));  //will be caught and customized by (@ControllerAdvice) class ItemNotFoundAdvicer
    }

//    @PutMapping("/brokers/{id}")
//    public Broker update(@RequestBody Broker e, @PathVariable String id) {
//        return brokerRepository.findById(id).map(entity -> {
//            entity.setName(e.getName());
//            entity.setCode(e.getCode());
//            return brokerRepository.save(entity);
//        }).orElseGet(() -> {
//            e.setId(id);
//            return brokerRepository.save(e);
//        });
//    }

    @DeleteMapping("/brokers/{id}")
    public void delete(@PathVariable String id) {
        brokerRepository.deleteById(id);
    }
}
