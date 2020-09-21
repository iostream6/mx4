/*
 * 2020.09.19  - Created
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.List;
import mx4.springboot.model.DatedQuotes;
import mx4.springboot.model.QuoteType;

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
     * @param failed a list which will be populated with symbols for which quotes could not be successfully retrieved
     * @return if successful, a list of <code>DatedQuotes</code>, ordered by date from oldest to newest, with filled in stockQuotes, else an empty list
     */
    public List<DatedQuotes> getStockQuotes(List<String> symbols, LocalDate startDate, LocalDate endDate, QuoteType type, List<String> failed);
}
