/*
 * 2020.04.05 - Created
 * 2020.05.23 - QC'ed implementation of CRU for single user - Delete pending
 */
package mx4.springboot.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mx4.springboot.model.Portfolio;
import mx4.springboot.model.Portfolio.PortfolioUser;
import mx4.springboot.persistence.PortfolioRepository;
import mx4.springboot.persistence.PortfolioUserRepository;
import mx4.springboot.viewmodel.PortfolioViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioUserRepository portfolioUserRepository;

    /**
     * Creates a new portfolio record. By default, the requesting user is assumed to be the whole owner and is given write access to the created portfolio.
     *
     * @param uid the requesting user's userId
     * @param p the portfolio data model
     * @return A response entity with the created portfolio, including newly assigned portfolioId, in the response body
     */
    @PostMapping("/api/portfolios/{uid}")
    public ResponseEntity<?> create(@PathVariable String uid, @RequestBody PortfolioViewModel p) {//as long as this is an authenticated user with a valid user number in a role that can create portfolio & not disabled account etc
        if (p.getName() == null || p.getBrokerId() == null || p.getCode() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            portfolioRepository.save(((Portfolio) p));

            List<PortfolioUser> users = p.getUsers();

            if (users == null || users.isEmpty()) {
                users = new ArrayList<>();
                final PortfolioUser pu = new PortfolioUser(p.getId(), uid, true, true, 100);  //String portfolioID, String userCode, boolean read, boolean write, double stake
                portfolioUserRepository.save(pu);
                users.add(pu);
                p.setUsers(users);
            } else {
                users.stream().forEachOrdered((pu) -> {
                    portfolioUserRepository.save(pu);//will update the ids
                });
                //no need to set users, reference in place
            }
            return ResponseEntity.ok(p);
        }
    }

    /**
     * Gets all portfolios associated with a given user.
     *
     * @param uid the requesting user'd userId
     * @return a list of one or more portfolios associated with the user
     */
    @GetMapping("/api/portfolios/{uid}")
    public List<PortfolioViewModel> readAll(@PathVariable String uid) {
        //TODO return PortfolioViewInfo DTO that also holds whether or not this portfolio is writatable by the user
        final List<PortfolioUser> portfolioUsers = portfolioUserRepository.findByUserId(uid);
        final List<PortfolioViewModel> pvms = new ArrayList<>();
        for (PortfolioUser portfolioUser : portfolioUsers) {
            final Optional<Portfolio> portfolioOptional = portfolioRepository.findById(portfolioUser.getPortfolioID());
            if (portfolioOptional.isPresent()) {
                final Portfolio p = portfolioOptional.get();
                final PortfolioViewModel pvm = new PortfolioViewModel();
                pvm.setId(p.getId());
                pvm.setBrokerId(p.getBrokerId());
                pvm.setCode(p.getCode());
                pvm.setName(p.getName());
                pvm.setUsers(portfolioUserRepository.findByPortfolioID(p.getId()));
                pvms.add(pvm);
            }
        }
        return pvms;
    }

    /**
     * Update basic attributes of an existing portfolio (e.g. name, code BUT NOT users,transactions, etc.).
     *
     * @param p the portfolio data model
     * @param uid the requesting user's userId
     * @return the updated portfolio model if successful, else an appropriate HTTP response conveying more info to the calling client.
     */
    @PutMapping("/api/portfolio/{uid}")
    public ResponseEntity<?> update(@RequestBody PortfolioViewModel p, @PathVariable String uid) {
        final Optional<PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, p.getId());
        if (portfolioUserOptional.isPresent()) {
            if (portfolioUserOptional.get().isWrite()) {
                portfolioRepository.save(((Portfolio) p));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

//    /**
//     * Deletes a portfolio, logged portfolio users and transactions, associated with a particular user. To delete a portfolio, the requesting user must have write privilege on that portfolio.
//     * Alternatively, the user must have the ADMIN role and use the admin features/endpoints.
//     *
//     * @param uid the requesting user'd userId
//     * @param pid the subject portfolio portfolioId
//     * @return
//     */
//    @DeleteMapping("api/portfolio/{uid}/{pid}")
//    public ResponseEntity<?> delete(@PathVariable String uid, @PathVariable String pid) {
//        final Optional<PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, pid);
//        if (portfolioUserOptional.isPresent()) {
//            if (portfolioUserOptional.get().isWrite()) {
//                //the requesting user has write permissions to this portfolio
//                //
//                // we must first delete all transactions associated with the portfolio, and delete all recorded PortfolioUsers of this portfolio
//                //              
//                portfolioUserRepository.deleteByPortfolioID(pid);
//                System.out.println("\n\nTODO DELETE TRANSACTIONS!!!\n\n");
//                
//                //
//                //
//                //delete the portfolio
//                portfolioRepository.deleteById(pid);
//                return ResponseEntity.ok(pid);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//
//    }
//    //
//    //
//    //
//    //
//    //
//    //
//    //TODO looks wrong!!
//    @GetMapping("/admin/portfolios/{uc}")// only allowed for special roles - basically sees all portfolios from all users
//    public List<Portfolio> priviledgedReadAllForUser(@PathVariable String uc) {
//        final List<PortfolioUser> pus = portfolioUserRepository.findByUserCode(uc);
//        final List<Portfolio> portfolios = new ArrayList<>();
//
//        pus.stream().map((pu) -> portfolioRepository.findById(pu.getPortfolioID())).filter((po) -> (po.isPresent())).forEachOrdered((po) -> {
//            portfolios.add(po.get());
//        });
//        return portfolios;
//    }
//
//    @DeleteMapping("/portfolios/{uc}/{pid}")
//    public void delete2(@PathVariable String uc, @PathVariable String pid) {//as long as this is an authenticated user with a valid user number in a role that can create portfolio & not disabled account etc
//        //TODO vlaidation /aothorization
//        //delete
//        final List<PortfolioUser> pus = portfolioUserRepository.findByPortfolioID(pid);
//        if (pus.size() == 1) {
//            final PortfolioUser pu = pus.get(0);
//            if (pu.isWrite()) {
//                //this user can write to this portfolio
//                portfolioUserRepository.deleteByPortfolioIDAndUserCode(pid, uc);
//                portfolioRepository.deleteById(pid);
//            }
//        } else if (pus.size() > 1) {
////            // TODO adjust  stake of remaining userser? or worn that multiple users so can delete - first do portfoli update to have just one user
//            System.out.println("Hey Nig can delete as more than one u");
//        }
//    }
//
//    @PutMapping("/portfolios/{uc}")
//    public Portfolio update(@PathVariable String uc, @RequestBody Portfolio p) {
//
//        return portfolioRepository.findById(p.getId()).map(entity -> {
//            entity.setName(p.getName());
//            entity.setCode(p.getCode());
//
//            ///</</</ Update Portfolio Users
//            return portfolioRepository.save(entity);
//        }).orElseGet(() -> {
//            return portfolioRepository.save(p);
//        });
//    }
}
