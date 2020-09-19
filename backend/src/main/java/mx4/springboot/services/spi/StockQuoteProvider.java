/*
 * 2020.09.19  - Created
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.List;
import mx4.springboot.model.DateQuotes;
import mx4.springboot.model.Quote.QuoteType;

/**
 *
 * @author Ilamah, Osho
 */
public interface StockQuoteProvider {

    /**
     * Retrieves price quotes for the specified symbols between the stipulated dates.
     *
     * @param symbols the list of symbols for which the price quote is required
     * @param startDate the start date
     * @param endDate the end date
     * @param type the quote type
     * @return null if an error occurs or else a list of DateQuotes
     */
    public List<DateQuotes> getStockQuotes(List<String> symbols, LocalDate startDate, LocalDate endDate, QuoteType type);
}
