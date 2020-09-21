/*
 * 2020.04.03  - Created
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.List;
import mx4.springboot.model.Currency;
import mx4.springboot.model.DatedQuotes;
import mx4.springboot.model.QuoteType;

/**
 *
 * @author Ilamah, Osho
 */
public interface FXQuoteProvider {

    /**
     * Retrieves fx quotes for the specified currency symbols between the stipulated dates.
     *
     * @param currencies the list of currencies for which the fx quote is required
     * @param startDate the start date
     * @param endDate the end date
     * @param type the quote type
     * @param failed a list which will be populated with symbols for which quotes could not be successfully retrieved
     * @return if successful, a list of <code>DatedQuotes</code>, ordered by date from oldest to newest, with filled in fxQuotes, else an empty list
     */
    public List<DatedQuotes> getFXQuotes(List<Currency> currencies, LocalDate startDate, LocalDate endDate, QuoteType type, List<String> failed);
}
