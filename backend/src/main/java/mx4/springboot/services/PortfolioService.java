/*
 * 2020.04.05 - Created
 * 2020.05.23 - QC'ed implementation of CRU for single user - Delete pending
 * 2020.05.29 - Implemented portfolio delete endpoint. Added user role CRUD endpoints for transactions.
 * 2020.06.05 - Method to read all transactions in all portfolios owned/readable by a given user implemented.
 * 2020.06.06 - Added multiple transactions delete endpoint
 * 2020.06.07 - Added createTransactionFromLegacyData method to handle transaction migration from Excel
 */
package mx4.springboot.services;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import mx4.springboot.model.Instrument;
import mx4.springboot.model.Portfolio;
import mx4.springboot.model.Transaction;
import mx4.springboot.persistence.InstrumentRepository;
import mx4.springboot.persistence.PortfolioRepository;
import mx4.springboot.persistence.PortfolioUserRepository;
import mx4.springboot.persistence.TransactionRepository;
import mx4.springboot.viewmodel.PortfolioViewModel;
import org.openide.util.Exceptions;
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
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioUserRepository portfolioUserRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final Comparator<Transaction> dataTransactionComporator = (Transaction a, Transaction b) -> {
        return a.getDate().compareTo(b.getDate());
    };

    /**
     * Creates a new portfolio record. By default, the requesting user is assumed to be the whole owner and is given read/write access to the created portfolio.
     *
     * @param uid the requesting user's userId
     * @param p the portfolio data model
     * @return A response entity with the created portfolio, including newly assigned portfolioId, in the response body
     */
    @PostMapping("/api/portfolios/{uid}")
    public ResponseEntity<?> createPortfolio(@PathVariable String uid, @RequestBody PortfolioViewModel p) {//as long as this is an authenticated user with a valid user number in a role that can create portfolio & not disabled account etc
        if (p.getName() == null || p.getBrokerId() == null || p.getCode() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            portfolioRepository.save(((Portfolio) p));
            List<Portfolio.PortfolioUser> users = p.getUsers();
            if (users == null || users.isEmpty()) {
                users = new ArrayList<>();
                final Portfolio.PortfolioUser pu = new Portfolio.PortfolioUser(p.getId(), uid, true, true, 100);  //String portfolioID, String userCode, boolean read, boolean write, double stake
                portfolioUserRepository.save(pu);
                users.add(pu);
                p.setUsers(users);
            } else {
                users.stream().forEachOrdered((pu) -> {
                    portfolioUserRepository.save(pu);//will update the ids
                });
            }
            return ResponseEntity.ok(p);
        }
    }

    /**
     * Gets all portfolios associated with a given user.
     *
     * @param uid the requesting user'd userId
     * @return a list of zero or more portfolios associated with the user
     */
    @GetMapping("/api/portfolios/{uid}")
    public List<PortfolioViewModel> readPortfolios(@PathVariable String uid) {
        final List<Portfolio.PortfolioUser> portfolioUsers = portfolioUserRepository.findByUserId(uid);
        final List<PortfolioViewModel> pvms = new ArrayList<>();
        for (Portfolio.PortfolioUser portfolioUser : portfolioUsers) {
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
    public ResponseEntity<?> updatePortfolio(@RequestBody PortfolioViewModel p, @PathVariable String uid) {
        final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, p.getId());
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

    /**
     * Deletes a portfolio, logged portfolio users and transactions, associated with a particular user. To delete a portfolio, the requesting user must have write privilege on that portfolio.
     * Alternatively, the user must have the ADMIN role and use the admin features/endpoints.
     *
     * @param uid the requesting user'd userId
     * @param pid the subject portfolio's id
     * @return
     */
    @DeleteMapping("api/portfolio/{uid}/{pid}")
    public ResponseEntity<?> deletePortfolio(@PathVariable String uid, @PathVariable String pid) {
        final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, pid);
        if (portfolioUserOptional.isPresent()) {
            if (portfolioUserOptional.get().isWrite()) {
                //the requesting user has write permissions to this portfolio:: we must first delete all transactions associated with the portfolio, 
                //and delete all recorded PortfolioUsers of this portfolio, and then we can delete the portfolio
                transactionRepository.deleteByPortfolioId(pid);
                portfolioUserRepository.deleteByPortfolioID(pid);
                portfolioRepository.deleteById(pid);
                return ResponseEntity.ok(pid);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    //
    // -- Transactions
    //
    /**
     * Creates a new user transaction.
     *
     * @param t the transaction data model
     * @param uid the associated user's id
     * @return A response entity with the created transaction, including newly assigned id, in the response body
     */
    @PostMapping("/api/transactions/{uid}")
    public ResponseEntity<?> createTransaction(@RequestBody Transaction t, @PathVariable String uid) {
        //check fields not null
        if (t.getCurrencyId() != 0 || t.getDate() != null || t.getInstrumentId() != null || t.getPortfolioId() != null || t.getType() != null) {
            final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, t.getPortfolioId());
            if (portfolioUserOptional.isPresent() && portfolioUserOptional.get().isWrite()) {
                return ResponseEntity.ok(transactionRepository.save(t));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    /**
     * Gets all transactions in a portfolio associated with a given user.
     *
     * @param uid the requesting user's id
     * @param pid the target portfolio id
     * @return a list of transactions in the portfolio, provided the user has read access
     */
    @GetMapping("/api/transactions/{uid}/{pid}")
    public ResponseEntity<?> readPortfolioTransactions(@PathVariable String uid, @PathVariable String pid) {
        final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, pid);
        if (portfolioUserOptional.isPresent() && portfolioUserOptional.get().isRead()) {
            return ResponseEntity.ok(transactionRepository.findByPortfolioId(pid));
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    /**
     * Gets all transactions from all portfolios associated with a given user.
     *
     * @param uid the requesting user's id
     * @return a list of transactions in the user's portfolios, provided the user has read access
     */
    @GetMapping("/api/transactions/{uid}")
    public ResponseEntity<?> readUserTransactions(@PathVariable String uid) {
        final List<Portfolio.PortfolioUser> pus = portfolioUserRepository.findByUserId(uid);
        final List<Transaction> transactions = new ArrayList<>();
        for (final Portfolio.PortfolioUser pu : pus) {
            if (pu.isRead()) {
                transactions.addAll(transactionRepository.findByPortfolioId(pu.getPortfolioID()));
            }
        }
        //
        Collections.sort(transactions, dataTransactionComporator);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Updates a given transaction. The portfolio id and transaction id will not be changed.
     *
     * @param t the transaction data model
     * @param uid the associated user's id
     * @param tid the transaction's id
     * @return
     */
    @PutMapping("/api/transaction/{uid}/{tid}")
    public ResponseEntity<?> updateTransaction(@RequestBody Transaction t, @PathVariable String uid, @PathVariable String tid) {
        final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, t.getPortfolioId());
        if (portfolioUserOptional.isPresent() && portfolioUserOptional.get().isWrite()) {
            final Optional<Transaction> transactionOptional = transactionRepository.findById(tid);
            if (transactionOptional.isPresent()) {

                final Transaction inDB = transactionOptional.get();
                //transaction and portfolio id must not change and must be consistent with what the user says they want to updated (as per URL)
                t.setId(inDB.getId());
                t.setPortfolioId(inDB.getPortfolioId());
                //
                return ResponseEntity.ok(transactionRepository.save(t));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    /**
     * Deletes a transaction from a portfolio. The requesting user must have write privilege on that portfolio. Alternatively, the user must have the ADMIN role and use the admin features/endpoints.
     *
     * @param uid the requesting user's id
     * @param tid the subject transaction's id
     */
    @DeleteMapping("/api/transaction/{uid}/{tid}")
    public void deleteTransaction(@PathVariable String uid, @PathVariable String tid) {
        final Optional<Transaction> transactionOptional = transactionRepository.findById(tid);
        if (transactionOptional.isPresent()) {
            final Transaction t = transactionOptional.get();
            final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, t.getPortfolioId());
            if (portfolioUserOptional.isPresent() && portfolioUserOptional.get().isWrite()) {
                transactionRepository.deleteById(tid);
            }
        }
    }

    /**
     * Deletes one of more of a users transactions. The requesting user must have write privilege on associated portfolios. Alternatively, the user must have the ADMIN role and use the admin
     * features/endpoints.
     *
     * @param uid the requesting user's id
     * @param tids a comma-separated list of transaction id's to be deleted
     */
    @DeleteMapping("/api/transactions/{uid}/{tids}")
    public void deleteTransactions(@PathVariable String uid, @PathVariable String tids) {
        final String[] ids = tids.split(",");
        for (final String tid : ids) {
            final Optional<Transaction> transactionOptional = transactionRepository.findById(tid);
            if (transactionOptional.isPresent()) {
                final Transaction t = transactionOptional.get();
                final Optional<Portfolio.PortfolioUser> portfolioUserOptional = portfolioUserRepository.findByUserIdAndPortfolioID(uid, t.getPortfolioId());
                if (portfolioUserOptional.isPresent() && portfolioUserOptional.get().isWrite()) {
                    transactionRepository.deleteById(tid);
                }
            }
        }
    }

    @Autowired
    private InstrumentRepository instrumentRepository;

    /**
     * Creates new user transactions from the legacy (spreadsheet) data. Expects a single string containing all the rows of tab delimited values from INCLUDE to TAX. This string parameter may be
     * supplied as text/plain (e.g. via postman or similar). Ensure that the Portfolios (with the right code
     *
     * @param data a single string containing all the rows of tab delimited values from INCLUDE to TAX
     * @param uid the associated user's id
     * @return A response entity with the created transaction, including newly assigned id, in the response body
     */
    @PostMapping("/admin/t/from/legacy/{uid}")
    public ResponseEntity<?> createTransactionFromLegacyData(@RequestBody String data, @PathVariable String uid) throws Exception {

        final List<Instrument> instruments = instrumentRepository.findAll();
        
        final List<Portfolio.PortfolioUser> portfolioUsers = portfolioUserRepository.findByUserId(uid);
        final ArrayList<Portfolio> portfolios = new ArrayList<>();

        for (final Portfolio.PortfolioUser pu : portfolioUsers) {
            Optional<Portfolio> po = portfolioRepository.findById(pu.getPortfolioID());
            if (po.isPresent()) {
                portfolios.add(po.get());
            }
        }
        final Scanner lineScanner = new Scanner(data);
        final int PROVISIONAL_INDEX = 0, DATE_INDEX = 2, PORTFOLIO_INDEX = 3, CURRENCY_INDEX = 4, TYPE_INDEX = 5, INSTRUMENT_INDEX = 6,
                UNITS_INDEX = 7, AMOUNT_PER_UNIT_INDEX = 8, FEES_INDEX = 10, TAXES_INDEX = 11;
        final String ONE = "1";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final DecimalFormat nf = new DecimalFormat("#0.00##");
        final ArrayList<Transaction> transactions = new ArrayList<>();
        String temp;
        while (lineScanner.hasNextLine()) {
            final String[] lineData = lineScanner.nextLine().split("\t");
            final Transaction t = new Transaction();
            //
            try {
                t.setProvisional(lineData[PROVISIONAL_INDEX].equals(ONE));
                t.setDate(sdf.parse(lineData[DATE_INDEX]));
                //portfolio
                for (final Portfolio p : portfolios) {
                    if (p.getCode().equals(lineData[PORTFOLIO_INDEX])) {
                        t.setPortfolioId(p.getId());
                        break;
                    }
                }
                if (t.getPortfolioId() == null) {
                    throw new Exception("Could not resolve portfolio name::" + lineData[PORTFOLIO_INDEX]);
                }
                // currency
                switch (lineData[CURRENCY_INDEX]) {
                    case "GBPX":
                    case "GBX":
                        t.setCurrencyId(1001104);//GBX CURRENCY
                        break;
                    case "USDX"://for US we will always use dollars
                    case "USX":
                        t.setCurrencyId(1001101);//USD CURRENCY
                        break;
                    case "EURX"://for EUR we will always use Euros
                    case "EUX":
                        t.setCurrencyId(1001105);//EUR CURRENCY
                        break;
                }
                //
                for (final Instrument i : instruments){
                    if(i.getCode().equals(lineData[INSTRUMENT_INDEX])){
                        t.setInstrumentId(i.getId());
                        break;
                    }
                }
                if (t.getInstrumentId() == null) {
                    System.out.println("\n\n\nCould not resolve instrument name:: " + lineData[INSTRUMENT_INDEX]);
                    //throw new Exception("Could not resolve instrument name:: " + lineData[INSTRUMENT_INDEX]);
                    continue;
                }
                //
                t.setType(Transaction.Type.valueOf(lineData[TYPE_INDEX]));
                t.setUnits(Double.parseDouble(lineData[UNITS_INDEX]));
                t.setAmountPerUnit(Double.parseDouble(lineData[AMOUNT_PER_UNIT_INDEX]));
                t.setFees(Double.parseDouble(lineData[FEES_INDEX]));
                t.setTaxes(Double.parseDouble(lineData[TAXES_INDEX]));
                if (t.getCurrencyId() != 1001104) {//correct from sheet (USDX, EURX to USD, EUR)
                    temp = nf.format(t.getAmountPerUnit() / 100.0);
                    t.setAmountPerUnit(Double.parseDouble(temp));
                    //
                    temp = nf.format(t.getFees() / 100.0);
                    t.setFees(Double.parseDouble(temp));
                    //
                    temp = nf.format(t.getTaxes() / 100.0);
                    t.setTaxes(Double.parseDouble(temp));
                }
                transactions.add(t);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        transactionRepository.saveAll(transactions);
        return ResponseEntity.ok().build();
    }
}
