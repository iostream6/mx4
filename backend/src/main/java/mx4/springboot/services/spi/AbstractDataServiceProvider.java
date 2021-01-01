/*
 * 2020.09.19  - Created
 * 2020.09.22  - Improved implementation - added merge + rely on updated data model
 * 2021.01.01  - Improved merge implementation to deal with general cases
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

/**
 * Provides implementation of methods that are common to data (stock quote, fx rates and fin data) service implementations
 *
 * @author Ilamah, Osho
 */
public abstract class AbstractDataServiceProvider {

    protected static Map<String, String> SYMBOL_MAP;
    protected static boolean debug;

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
    
    protected boolean merge(final List<DatedQuotes> equityQuotes, final List<DatedQuotes> fxQuotes){
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
            if(!merged){
                return false;//could not find FX dates for this stock quotes! Fail gracefully
            }
        }
        
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
