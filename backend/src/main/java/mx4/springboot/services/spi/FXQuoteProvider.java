/*
 * 2020.04.03  - Created
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.List;
import mx4.springboot.model.Currency;
import mx4.springboot.model.Currency.Exchange;
import mx4.springboot.model.Quote;

/**
 *
 * @author Ilamah, Osho
 */
public interface FXQuoteProvider {

    /**
     * Retrieves latest Exchange rates for the specified symbols.
     * 
     * @param currencies the currencies for which exchange rates are required
     * @param startDate
     * @param endDate
     * @param type
     * @return if successful, a list of exchange rates, ordered by date from oldest to newest, else an empty list 
     */
    List<Exchange> getExchangeRates(List<Currency> currencies, LocalDate startDate, LocalDate endDate, Quote.QuoteType type);
}
