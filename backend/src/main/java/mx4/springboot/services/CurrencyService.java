/*
 * 2020.04.03  - Created | Base functionality QC'ed
 * 2020.05.01  - Path security QC'd
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

    private static final String TYPE = "Currency";

    @Autowired
    private ExchangeRepository exchangeRepository;

    private static final List<Currency> currencyList;

    @GetMapping("/currencies")
    public List<Currency> readAll() {
        return currencyList;
    }

    @GetMapping("/currencies/{id}")
    public Currency readOne(@PathVariable String id) {
        final long currencyID = Long.parseLong(id);
        final Optional<Currency> optional = currencyList.stream().filter(c -> c.getCurrencyID() == currencyID).findFirst();
        //return currencyList.stream().filter(x -> x.getCurrencyID() == currencyID).findAny().orElse(new Currency());
        return optional.orElseThrow(() -> new ItemNotFoundException(id, TYPE));  //will be caught and customized by (@ControllerAdvice) class ItemNotFoundAdvicer
    }

    //-------------------
    @GetMapping("/exchange/{from}/{to}")
    public Exchange convert(@PathVariable String from, @PathVariable String to) {
        final long fromCurrencyID = Long.parseLong(from);
        final long toCurrencyID = Long.parseLong(to);
        return exchangeRepository.findByFromIDAndToID(fromCurrencyID, toCurrencyID).orElseThrow(() -> new ItemNotFoundException(fromCurrencyID + "->" + toCurrencyID, TYPE));

    }

    @PutMapping("/exchange")//OK
    public List<Exchange> updateExchangRates() {
        ExchangeRateServiceProvider erp = Lookup.getDefault().lookup(ExchangeRateServiceProvider.class);
        if (erp != null) {
            final List<Exchange> exchanges = erp.fetchExchangeRates(currencyList);
            exchangeRepository.saveAll(exchanges);
            return exchanges;
        } else {
            throw new ItemNotFoundException("Exchange Rate Provider", TYPE);
        }
    }

    //initialize all supported currencies as a singleton list
    static {
        final List<Currency> currencies = new ArrayList();
        Currency currency = new Currency();
        //initialize King dollar:
        currency.setCode("USD");
        currency.setSymbol("$");
        currency.setCurrencyID(1001101);
        currency.setName("US Dollars");
        currencies.add(currency);

        //initilize the US Cents
        currency = new Currency();
        currency.setCode("USDX");
        currency.setSymbol("c");
        currency.setCurrencyID(1001102);
        currency.setName("US Cents");
        currencies.add(currency);

        //initilize the Queen's Pounds
        currency = new Currency();
        currency.setCode("GBP");
        currency.setSymbol("£");
        currency.setCurrencyID(1001103);
        currency.setName("GB Pounds");
        currencies.add(currency);

        //initilize the Queen's Pence
        currency = new Currency();
        currency.setCode("GBPX");
        currency.setSymbol("p");
        currency.setCurrencyID(1001104);
        currency.setName("GB Pence");
        currencies.add(currency);

        //initilize the Euro's
        currency = new Currency();
        currency.setCode("EUR");
        currency.setSymbol("€");
        currency.setCurrencyID(1001105);
        currency.setName("Euros");
        currencies.add(currency);

        //initilize the Queen's Pence
        currency = new Currency();
        currency.setCode("EURX");
        currency.setSymbol("c");
        currency.setCurrencyID(1001106);
        currency.setName("Euro Cents");
        currencies.add(currency);

        currencyList = Collections.unmodifiableList(currencies);

    }

}
