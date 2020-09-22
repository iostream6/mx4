/*
 * 2020.09.19  - Created
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.List;
import mx4.springboot.model.Currency;
import mx4.springboot.model.DatedQuotes;
import mx4.springboot.model.Instrument;
import mx4.springboot.model.QuoteType;

/**
 *
 * @author Ilamah, Osho
 */
public interface QuoteProvider {

    /**
     * Gets the name of the data provider service.
     *
     * @return
     */
    public String getName();

    /**
     * 
     * @param instruments the list of stock instruments for which stock price quotes are required
     * @param currencies the list of currencies for which fx quotes are required
     * @param startDate the start date
     * @param endDate the end date
     * @param type the quote type
     * @param failedStockQuotes a list which will be populated with instruments for which quotes could not be successfully retrieved
     * @param failedFXQuotes a list which will be populated with FX symbols for which quotes could not be successfully retrieved
     * @return if successful, a list of <code>DatedQuotes</code>, ordered by date from oldest to newest, with filled in stock and FX quotes, else an empty list
     */
    public List<DatedQuotes> getQuotes(List<Instrument> instruments, List<Currency> currencies, LocalDate startDate, LocalDate endDate, QuoteType type, List<Instrument> failedStockQuotes, List<String> failedFXQuotes);
}
