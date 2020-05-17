/*
 * 2020.04.02 - Created | Base functionality QC'ed
 * 2020.05.01  - Path security QC'd
 */
package mx4.springboot.services;

import java.util.List;
import java.util.Optional;
import mx4.springboot.model.Entity;
import mx4.springboot.persistence.EntityRepository;
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
//@RequestMapping("/api")
@RestController

public class EntityService {

    private static final String TYPE = "Entity";

    @Autowired
    private EntityRepository entityRepository;

    @PostMapping("/admin/entities") //Might also use ResponseEntity return type so we can set location. See Gutierez pp 101 & https://stackoverflow.com/a/51916728
    public Entity create(@RequestBody Entity e) {
        return entityRepository.save(e);
    }

    @GetMapping("/api/entities")
    public List<Entity> readAll() {
        return entityRepository.findAll();
    }

    @GetMapping("/api/entities/{id}")
    public Entity readOne(@PathVariable String id) {
        final Optional<Entity> optional = entityRepository.findById(id);
        return optional.orElseThrow(() -> new ItemNotFoundException(id, TYPE));  //will be caught and customized by (@ControllerAdvice) class ItemNotFoundAdvicer
    }

    @PutMapping("/admin/entities/{id}")
    public Entity update(@RequestBody Entity e, @PathVariable String id) {
        return entityRepository.findById(id).map(entity -> {
            entity.setName(e.getName());
            entity.setDescription(e.getDescription());
            return entityRepository.save(entity);
        }).orElseGet(() -> {
            e.setId(id);
            return entityRepository.save(e);
        });
    }

    @DeleteMapping("/admin/entities/{id}")
    public void delete(@PathVariable String id) {
        entityRepository.deleteById(id);
    }

//    public String welcome() {
//
////        entityRepository.deleteAll();
////
////        // save a couple of customers
////
////        //entityRepository.save(new Entity("Alice", 1));
////        //entityRepository.save(new Entity("Bob", 2));
////        
////        // fetch all customers
//////        System.out.println("Customers found with findAll():");
//////        System.out.println("-------------------------------");
//////        for (Entity e : entityRepository.findAll()) {
//////            System.out.println(e.getName() + " __" + e.getDescription());
//////        }
//////        System.out.println();
//////
//        return "<h1><font face='verdana'>Spring Boot Rocks!</font></h1>";
//
//    }
    /**
     * @Override public void run(String... args) throws Exception {
     *
     *
     *
     * // save a couple of customers repository.save(new Customer("Alice",
     * "Smith")); repository.save(new Customer("Bob", "Smith"));
     *
     * // fetch all customers System.out.println("Customers found with
     * findAll():"); System.out.println("-------------------------------"); for
     * (Customer customer : repository.findAll()) {
     * System.out.println(customer); } System.out.println();
     *
     * // fetch an individual customer System.out.println("Customer found with
     * findByFirstName('Alice'):");
     * System.out.println("--------------------------------");
     * System.out.println(repository.findByFirstName("Alice"));
     *
     * System.out.println("Customers found with findByLastName('Smith'):");
     * System.out.println("--------------------------------"); for (Customer
     * customer : repository.findByLastName("Smith")) {
     * System.out.println(customer); }
     *
     * }
     */
}
