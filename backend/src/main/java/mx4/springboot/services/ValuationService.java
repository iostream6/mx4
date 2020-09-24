/*
 * 2020.09.19  - Created
 * 2020.09.23  - Introduced minor tweaks and confirmed interop with frontend. Added Logging support.
 */
package mx4.springboot.services;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mx4.springboot.model.Currency;
import mx4.springboot.model.DatedQuotes;
import mx4.springboot.model.Instrument;
import mx4.springboot.model.Quote;
import mx4.springboot.model.QuoteType;
import mx4.springboot.persistence.InstrumentRepository;
import mx4.springboot.persistence.QuotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import mx4.springboot.services.spi.QuoteProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Provides support for valuation, including retrieving/persisting historic stock and FX quotes. Replaces legacy currency and value services.
 *
 * @author Ilamah, Osho
 */
@RestController
public class ValuationService {

    private static final Logger logger = LoggerFactory.getLogger(ValuationService.class);
    
    // Autowire the default data source implementation :: 
    //https://stackoverflow.com/questions/19026785/injecting-multiple-implementations-to-a-single-service-in-spring,  
    //http://zetcode.com/springboot/qualifier/
    @Autowired
    @Qualifier("default")
    private QuoteProvider qp;

    //
    @Autowired
    private QuotesRepository quotesRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    private static final List<Currency> currencyList;

// ---------------------------------------------------- CURRENCIES
    @GetMapping("/api/currencies")
    public List<Currency> readAll() {
        return currencyList;
    }
// ---------------------------------------------------- QUOTES

    /**
     * Creates EOM quotes records for the last month. Removes all prior records for the DB. Produces a log of which symbols it could not find so that those may be added to the record/DB manually (e.g.
     * via Postman) of an appropriately formatted String using
     *
     * @return A response entity with the created values record, including newly assigned id, in the response body
     */
    @PostMapping("/admin/quotes")
    public ResponseEntity<?> createQuotes() {
        final Runnable r = () -> {
            final LocalDate now = LocalDate.now();
            final LocalDate startDate = now.minusMonths(1);

            LocalDate cutOffDate = now.withDayOfMonth(1);   //cutoff date is the first day of this month on 'isBefore' criteria:: only records before this date are accepted
            Page<DatedQuotes> lastDateQuotesPage = quotesRepository.findAll(PageRequest.of(0, 1, Sort.Direction.DESC, "date"));

            if (lastDateQuotesPage.getNumberOfElements() > 0) {
                //existing records in the Quotes DB - check if we need to download new data!
                LocalDate lastDBQuoteDate = lastDateQuotesPage.getContent().get(0).getDate();
                if (lastDBQuoteDate.isAfter(startDate)) {
                    logger.info("EOM Data | Start@{}      End@{}| CutOff@{} | Last DB@{} -> Nothing to do", startDate, now, cutOffDate, lastDBQuoteDate);
                    return;
                }
            }

            final List<Instrument> instruments = instrumentRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
            final List<Instrument> failedStockQuotes = new ArrayList<>();
            final List<String> failedFXQuotes = new ArrayList<>();

            final List<DatedQuotes> rsQuotes = qp.getQuotes(instruments, currencyList, startDate, now, QuoteType.EOM, failedStockQuotes, failedFXQuotes);

            if (rsQuotes.isEmpty() == false) {
                final DecimalFormat nf = new DecimalFormat();
                nf.setMinimumFractionDigits(4);
                nf.setMaximumFractionDigits(4);
                final List<DatedQuotes> quotes = rsQuotes.stream().filter(dqs -> {
                    return dqs.getFxQuotes() != null && dqs.getStockQuotes() != null && dqs.getDate().isBefore(cutOffDate);
                }).collect(Collectors.toList());
                logger.info("\n\n\n --- Historic quotes from {} ---", qp.getName());
                quotes.stream().forEach(dqs -> {
                    logger.info("{} --------------------------", dqs.getDate());
                    dqs.getStockQuotes().stream().forEach(q -> logger.info("STOCK\t{}\t{}", getSymbolFromId(instruments, q.getCode()), nf.format(q.getValue())));
                    logger.info("\t\t\t");
                    dqs.getFxQuotes().stream().forEach(q -> logger.info("FX\t{}\t{}", q.getCode(), nf.format(q.getValue())));
                    logger.info("*************************************");
                });
                quotesRepository.deleteAll();
                quotesRepository.saveAll(quotes);
            }

            if (failedStockQuotes.isEmpty() == false) {
                logger.info("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                failedStockQuotes.stream().forEach(f -> {
                    logger.warn(" --- {} historic quotes not found by {}", f.getCode(), qp.getName());//print the friendly name
                });
            }
            if (failedFXQuotes.isEmpty() == false) {
                logger.info("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                failedFXQuotes.stream().forEach(f -> {
                    logger.warn(" --- {} historic FX quotes not found by {}", f, qp.getName());
                });
            }
        };
        (new Thread(r)).start();

        return ResponseEntity.ok().build();
    }

    /**
     * Reads the latest DatedQuotes records from the database
     *
     * @return the latest DatedQuotes records in the database
     */
    @GetMapping("/api/quotes/last")
    public ResponseEntity<?> readLastFXQuotes() {
        List<DatedQuotes> dq = quotesRepository.findAll(PageRequest.of(0, 1, Sort.Direction.DESC, "date")).toList();
        if (dq.isEmpty()) {
            return ResponseEntity.notFound().build(); //sends http 404 
        }
        return ResponseEntity.ok(dq.get(0));
    }

    /**
     * Updates EOM quotes records for the last month. Parses and adds input stock quote data to the record/DB - if the DATE record already exists, the data is merge (without duplication) into the
     * existing date record. If the DATE record does not exist the quote data is NOT added to the database and a warning message is printed. Input data rows with FX types are ignored for now.
     *
     * @param data
     * @return
     */
    @PutMapping("/admin/quotes")
    public ResponseEntity<?> updateQuotesWithStringInput(@RequestBody String data) throws Exception {
        //DATE TYPE SYMBOL  VALUE
        //DATE TYPE SYMBOL  VALUE

        //where TYPE = STOCK | FX 
        final int DATE_INDEX = 0, TYPE_INDEX = 1, CODE_INDEX = 2, VALUE_INDEX = 3;

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DecimalFormat nf = new DecimalFormat("#0.00000##");
        final String STOCK_TYPE = "STOCK";
        final Scanner lineScanner = new Scanner(data);
       
        final List<Instrument> instruments = instrumentRepository.findAll();

        Map<LocalDate, DatedQuotes> inputDateQuotesMap = new HashMap<>();
        while (lineScanner.hasNextLine()) {
            final String[] lineData = lineScanner.nextLine().split("\t");
            final LocalDate d = LocalDate.parse(lineData[DATE_INDEX], dtf);

            DatedQuotes r = inputDateQuotesMap.get(d);
            if (r == null) {
                r = new DatedQuotes();
                r.setDate(d);
                r.setFxQuotes(new ArrayList<>());
                r.setStockQuotes(new ArrayList<>());
                inputDateQuotesMap.put(d, r);
            }
            boolean found = false;

            if (lineData[TYPE_INDEX].equals(STOCK_TYPE)) {
                for (Instrument i : instruments) {
                    if (i.getCode().equals(lineData[CODE_INDEX])) {
                        r.getStockQuotes().add(new Quote(i.getId(), nf.parse(lineData[VALUE_INDEX]).doubleValue()));
                        found = true;
                        logger.info("OK {}", lineData[CODE_INDEX]);
                        break;
                    }
                }
                if (!found) {
                    logger.warn("Could not find {}", lineData[CODE_INDEX]);
                }
            } else {
                //NOT currently supported as the auto create process works well for these
            }
        }

        final List<DatedQuotes> edqsList = quotesRepository.findAll(Sort.by(Sort.Direction.ASC, "date"));

        if (edqsList.isEmpty() == false) {
            logger.info("\n\n\n --- Existing quotes ***");
            edqsList.stream().forEach(dqs -> {
                logger.info("{} --------------------------", dqs.getDate());
                dqs.getStockQuotes().stream().forEach(q ->  logger.info("STOCK\t{}\t{}", getSymbolFromId(instruments, q.getCode()),nf.format(q.getValue())));
                logger.info("*************************************");
            });
        }

        inputDateQuotesMap.values().forEach((idq) -> {
            final LocalDate iDate = idq.getDate();
            for (final DatedQuotes edqs : edqsList) {
                if (edqs.getDate().equals(iDate)) {
                    addOrReplace(edqs.getStockQuotes(), idq.getStockQuotes());
                }
            }
        });

        if (edqsList.isEmpty() == false) {
            logger.info("\n\n\n --- Updated quotes ***");
            edqsList.stream().forEach(dqs -> {
                logger.info("{} --------------------------", dqs.getDate());
                dqs.getStockQuotes().stream().forEach(q -> logger.info("STOCK\t{}\t{}", getSymbolFromId(instruments, q.getCode()), nf.format(q.getValue())));
                logger.info("*************************************");
            });
        }

        quotesRepository.deleteAll();
        quotesRepository.saveAll(edqsList);
        return ResponseEntity.ok().build();
    }

    //initialize all supported currencies as a singleton list
    static {
        final List<Currency> currencies = new ArrayList();
        Currency currency = new Currency();
        //initialize King dollar:
        currency.setCode("USD");
        currency.setSymbol("$");
        currency.setId(1001101);
        currency.setName("US Dollars");
        currencies.add(currency);

        //initilize the US Cents
        currency = new Currency();
        currency.setCode("USX");
        currency.setSymbol("c");
        currency.setId(1001102);
        currency.setName("US Cents");
        currencies.add(currency);

        //initilize the Queen's Pounds
        currency = new Currency();
        currency.setCode("GBP");
        currency.setSymbol("£");
        currency.setId(1001103);
        currency.setName("GB Pounds");
        currencies.add(currency);

        //initilize the Queen's Pence
        currency = new Currency();
        currency.setCode("GBX");
        currency.setSymbol("p");
        currency.setId(1001104);
        currency.setName("GB Pence");
        currencies.add(currency);

        //initilize the Euros
        currency = new Currency();
        currency.setCode("EUR");
        currency.setSymbol("€");
        currency.setId(1001105);
        currency.setName("Euros");
        currencies.add(currency);

        //initilize the Euro Cents
        currency = new Currency();
        currency.setCode("EUX");
        currency.setSymbol("c");
        currency.setId(1001106);
        currency.setName("Euro Cents");
        currencies.add(currency);

        currencyList = Collections.unmodifiableList(currencies);

    }

    private String getSymbolFromId(List<Instrument> instruments, final String instrumentId) {
        for (Instrument i : instruments) {
            if (i.getId().equals(instrumentId)) {
                return i.getCode();
            }
        }
        return "Unknown";
    }

    private void addOrReplace(List<Quote> target, List<Quote> from) {
        final Stream<Quote> filteredFrom = from.stream().filter(q -> {
            for (Quote qq : target) {
                if (qq.getCode().equals(q.getCode())) {
                    return false;
                }
            }
            return true;
        });

        target.addAll(filteredFrom.collect(Collectors.toList()));

    }

}
