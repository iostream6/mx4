/*
 * 2020.09.19  - Created
 * 2020.09.22  - Improved implementation - added merge + rely on updated data model
 * 2021.01.01  - Improved merge implementation to deal with general cases
 * 2021.01.22  - Improved merge implementation - added 'strict' argument to provided better edge case control
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import mx4.springboot.model.DatedQuotes;
import mx4.springboot.model.Instrument;
import mx4.springboot.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides implementation of methods that are common to data (stock quote, fx rates and fin data) service implementations
 *
 * @author Ilamah, Osho
 */
public abstract class AbstractDataServiceProvider {

    protected static Map<String, String> SYMBOL_MAP;
    protected static boolean debug;
    
    protected static final Logger logger = LoggerFactory.getLogger(AlphaVantageDataServiceProvider.class);

    //
    protected static final String UA = "User-Agent", ACCEPT_CHARSET = "Accept-Charset", UA_VALUE = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36", GET_METHOD = "GET";
    protected static final String ACCEPT_CHARSET_VALUE = java.nio.charset.StandardCharsets.UTF_8.name();

    /**
     * Maps user-space provided quote symbols to data service-space symbols
     *
     * @param instruments a list of instruments with user-space stock codes
     * @return a list of quote service-space symbols
     */
    protected static List<String> mapSymbols(List<Instrument> instruments) {
        final List<String> mappedSymbols = new ArrayList<>();
        instruments.stream().forEach(s -> {
            final String symbol = SYMBOL_MAP.get(s.getCode());
            mappedSymbols.add(symbol == null ? s.getCode() : symbol);
        });
        return mappedSymbols;
    }

    /**
     * Creates a LocalDate which contains the same day/month/year information as the input Calendar
     *
     * @param c the input Calendar object
     * @return a LocalDate which contains the same day/month/year information as the input Calendar
     */
    protected static LocalDate getDate(final Calendar c) {
        return LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }
       
    protected boolean merge(final List<DatedQuotes> equityQuotes, final List<DatedQuotes> fxQuotes, final boolean strict){
        List<DatedQuotes> unmerged = new ArrayList<>(equityQuotes.size());
        
        for(final DatedQuotes deq : equityQuotes){
            LocalDate date = deq.getDate();
            boolean merged = false;
            for (int i = 0; i < fxQuotes.size(); i++) {
                if(fxQuotes.get(i).getDate().equals(date)){
                    final List<Quote.FXQuote> fxQuote = fxQuotes.get(i).getFxQuotes();
                    deq.setFxQuotes(fxQuote);                   
                    merged = true;
                    break;
                }
            }
            if(merged == false){
                final StringBuilder x = new StringBuilder();
                fxQuotes.forEach(e -> {
                    x.append(" ");
                    x.append(e.getDate().toString());
                });
                logger.warn("FX quotes counterparts not found for '{}' Stock Quotes. FX Dates are: {}", date.toString(), x.toString());
                //could not find FX dates for this stock quotes! Fail gracefully if strict is strue
                //
                // Example would be equityQuotes (Stocks: 2020.12.31, 2021.01.21); fxQuotes (FX: 2020.12.31, 2021.01.22) 
                // with strict set to false, equityQuotes becomes (Stocks+FX: 2020.12.31) based on drop below
                // with strict set to true, process fails and false is returned
                if(strict){
                    return false;
                }else{
                    //collect the stock quotes that dont have same dated fx quotes
                    unmerged.add(deq);
                }
            }
        }
        equityQuotes.removeAll(unmerged);// in the unstrict case, we want to drop all unmerged stock quotes
        return true;
    }

    public static class DatedQuote extends Quote {

        public DatedQuote(LocalDate date, String code, double value) {
            super(code, value);
            this.date = date;
        }
        private LocalDate date;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

    }

    public static class DatedFXQuote extends Quote.FXQuote {

        public DatedFXQuote(LocalDate date, long from, long to, String code, double value) {
            super(from, to, code, value);
            this.date = date;
        }

        private LocalDate date;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }
}
