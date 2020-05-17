/*
 * 2020.04.05 - Created
 */
package mx4.springboot.services;

import java.util.ArrayList;
import java.util.List;
import mx4.springboot.model.Portfolio;
import mx4.springboot.model.Portfolio.PortfolioUser;
import mx4.springboot.persistence.PortfolioRepository;
import mx4.springboot.persistence.PortfolioUserRepository;
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
//TODO security
@RestController

public class PortfolioService {

    private static final String TYPE = "Portfolio";

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioUserRepository portfolioUserRepository;

//    // only allowed for special roles - basically can create a portfolio for all users
//    @PostMapping("/portfolios") //Might also use ResponseEntity return type so we can set location. See Gutierez pp 101 & https://stackoverflow.com/a/51916728
//    public Broker priviledgedCreate(@RequestBody Broker e) {
//        return brokerRepository.save(e);
//    }
//
//    @GetMapping("/portfolios")// only allowed for special roles - basically sees all portfolios from all users
//    public List<Broker> priviledgedReadAll() {
//        return brokerRepository.findAll();
//    }
//
//  Update portfolio Users - ADMIN priviledge    
    @PostMapping("/portfolios/{uc}")
    public Portfolio create(@PathVariable String uc, @RequestBody Portfolio p) {//as long as this is an authenticated user with a valid user number in a role that can create portfolio & not disabled account etc
        //TODO vlaidation /aothorization
        //creation
        p = portfolioRepository.save(p);
        final PortfolioUser pu = new PortfolioUser(p.getId(), uc, true, true, 100);  //String portfolioID, String userCode, boolean read, boolean write, double stake
        portfolioUserRepository.save(pu);
        return p;
    }

    //TODO looks wrong!!
    @GetMapping("/admin/portfolios/{uc}")// only allowed for special roles - basically sees all portfolios from all users
    public List<Portfolio> priviledgedReadAllForUser(@PathVariable String uc) {
        final List<PortfolioUser> pus = portfolioUserRepository.findByUserCode(uc);
        final List<Portfolio> portfolios = new ArrayList<>();
        
        pus.stream().map((pu) -> portfolioRepository.findById(pu.getPortfolioID())).filter((po) -> (po.isPresent())).forEachOrdered((po) -> {
            portfolios.add(po.get());
        });
        return portfolios;
    }

    @DeleteMapping("/portfolios/{uc}/{pid}")
    public void delete(@PathVariable String uc, @PathVariable String pid) {//as long as this is an authenticated user with a valid user number in a role that can create portfolio & not disabled account etc
        //TODO vlaidation /aothorization
        //delete
        final List<PortfolioUser> pus = portfolioUserRepository.findByPortfolioID(pid);
        if (pus.size() == 1) {
            final PortfolioUser pu = pus.get(0);
            if (pu.isWrite()) {
                //this user can write to this portfolio
                portfolioUserRepository.deleteByPortfolioIDAndUserCode(pid, uc);
                portfolioRepository.deleteById(pid);
            }
        } else if (pus.size() > 1) {
//            // TODO adjust  stake of remaining userser? or worn that multiple users so can delete - first do portfoli update to have just one user
            System.out.println("Hey Nig can delete as more than one u");
        }
    }

    @PutMapping("/portfolios/{uc}")
    public Portfolio update(@PathVariable String uc, @RequestBody Portfolio p) {

        return portfolioRepository.findById(p.getId()).map(entity -> {
            entity.setName(p.getName());
            entity.setCode(p.getCode());

            ///</</</ Update Portfolio Users
            return portfolioRepository.save(entity);
        }).orElseGet(() -> {
            return portfolioRepository.save(p);
        });
    }

//    
//    @GetMapping("/user/{uid}/portfolios/")
//    public List<Broker> readAll(@PathVariable String uid) {
//        return null;
//    }
//
//    @GetMapping("/user/{uid}/portfolios/{pid}")
//    public Broker readOne(@PathVariable String uid) {
//
//        return null;
//
////        final Optional<Broker> optional = brokerRepository.findById(id);
////        return optional.orElseThrow(() -> new ItemNotFoundException(id, TYPE));  //will be caught and customized by (@ControllerAdvice) class ItemNotFoundAdvicer
//    }
//
//    @PutMapping("/brokers/{id}")
//    public Broker update(@RequestBody Broker e, @PathVariable String id) {
//        return portfolioRepository.findById(id).map(entity -> {
//            entity.setName(e.getName());
//            entity.setCode(e.getCode());
//            return portfolioRepository.save(entity);
//        }).orElseGet(() -> {
//            e.setId(id);
//            return portfolioRepository.save(e);
//        });
//    }
//
//    @DeleteMapping("/brokers/{id}")
//    public void delete(@PathVariable String id) {
//        portfolioRepository.deleteById(id);
//    }
}
