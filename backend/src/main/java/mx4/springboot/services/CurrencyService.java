/*
 * 2020.04.03  - Created | Base functionality QC'ed
 * 2020.05.01  - Path security QC'd
 * 2020.05.23  - Removed "readOne" and use ResponseEntity return values
 */
package mx4.springboot.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mx4.springboot.persistence.ExchangeRepository;
import mx4.springboot.services.spi.ExchangeRateServiceProvider;
import mx4.springboot.model.Currency;
import mx4.springboot.model.Currency.Exchange;
import org.openide.util.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ilamah, Osho
 */
@RestController
@RequestMapping("/api")
public class CurrencyService {

    @Autowired
    private ExchangeRepository exchangeRepository;

    private static final List<Currency> currencyList;

    @GetMapping("/currencies")
    public List<Currency> readAll() {
        return currencyList;
    }

    //-------------------
    @GetMapping("/exchange/{from}/{to}")
    public ResponseEntity<?> convert(@PathVariable String from, @PathVariable String to) {
        try {
            final long fromCurrencyID = Long.parseLong(from);
            final long toCurrencyID = Long.parseLong(to);
            final Optional<Exchange> xeOptional = exchangeRepository.findByFromIDAndToID(fromCurrencyID, toCurrencyID);
            if (xeOptional.isPresent()) {
                ResponseEntity.ok(xeOptional.get());
            }
        } catch (Exception e) {
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/exchanges")//TODO Please check
    public List<Exchange> updateExchangRates() {
        ExchangeRateServiceProvider erp = Lookup.getDefault().lookup(ExchangeRateServiceProvider.class);
        if (erp != null) {
            final List<Exchange> exchanges = erp.fetchExchangeRates(currencyList);
            exchangeRepository.saveAll(exchanges);
            return exchanges;
        } else {
            return Collections.EMPTY_LIST;
        }
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

}
