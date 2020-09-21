/*
 * 2020.09.19  - Created
 */
package mx4.springboot.services.spi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
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
     * @param symbols a list of user-space symbols
     * @return a list of quote service-space symbols
     */
    protected static List<String> mapSymbols(List<String> symbols) {
        final List<String> mappedSymbols = new ArrayList<>();
        symbols.stream().forEach(s -> {
            final String symbol = SYMBOL_MAP.get(s);
            mappedSymbols.add(symbol == null ? s : symbol);
        });
        //String[] mappedSymbolArray = new String[mappedSymbols.size()];
        //mappedSymbols.toArray(mappedSymbolArray);
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

    /**
     * Gets the name of the data provider service.
     *
     * @return
     */
    public abstract String getName();

    public static class DatedQuote extends Quote {

        public DatedQuote(LocalDate date, String symbol, double value) {
            super(symbol, value);
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

        public DatedFXQuote(LocalDate date, long from, long to, String symbol, double value) {
            super(from, to, symbol, value);
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
