/*
 * 2020.09.19  - Created
 */
package mx4.springboot.services.spi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import mx4.springboot.model.Currency;
import mx4.springboot.model.DatedQuotes;
import mx4.springboot.model.Quote;

import mx4.springboot.model.Quote.FXQuote;
import mx4.springboot.model.QuoteType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ilamah, Osho
 */
//@ServiceProviders({
//    @ServiceProvider(service = StockQuoteProvider.class, position = 1),
//    @ServiceProvider(service = FXQuoteProvider.class, position = 20)})
@Component
//make sure other implementations are not defaulted or autowire list and select at runtime 
//https://stackoverflow.com/questions/19026785/injecting-multiple-implementations-to-a-single-service-in-spring,  
//http://zetcode.com/springboot/qualifier/
@Qualifier("default")
public class AlphaVantageDataServiceProvider extends AbstractDataServiceProvider implements StockQuoteProvider, FXQuoteProvider {

    private static final String MONTHLY_ADJ_STOCK_QUOTE = "TIME_SERIES_MONTHLY_ADJUSTED", MONTHLY_FX_QUOTE = "FX_MONTHLY";
    private static final String DAILY_ADJUSTED_STOCK_QUOTE = "TIME_SERIES_DAILY_ADJUSTED", DAILY_FX_QUOTE = "FX_DAILY";
    private static final String PRICE_CSV_URL_TEMPLATE = "https://www.alphavantage.co/query?function=%s&symbol=%s&apikey=%s&datatype=csv";
    private static final String FX_CSV_URL_TEMPLATE = "https://www.alphavantage.co/query?function=%s&from_symbol=%s&to_symbol=%s&apikey=%s&datatype=csv";
    private static final String API_ERROR_PREFIX = "{", SERVICE_NAME = "AlphaVantage Quote Service";

    //
    @Value("${app.alphavantage.apikey}")
    private String API_KEY;

    public AlphaVantageDataServiceProvider() {
        SYMBOL_MAP = new HashMap<>();
        // add mapping for instrument symbols which need special help to map to the AlphaVantage stock symbol
        SYMBOL_MAP.put("AIR", "AIR.PA");         //
        //
        // LSE shares:: https://github.com/prediqtiv/alpha-vantage-cookbook/blob/master/symbol-lists.md#united-kindom
        SYMBOL_MAP.put("BATS", "BATS.L");
        SYMBOL_MAP.put("BRSC", "BRSC.L");
        SYMBOL_MAP.put("DGE", "DGE.L");
        SYMBOL_MAP.put("FGT", "FGT.L");
        SYMBOL_MAP.put("GGRP", "GGRP.L");
        SYMBOL_MAP.put("HSBA", "HSBA.L");
        SYMBOL_MAP.put("IMB", "IMB.L");
        SYMBOL_MAP.put("LGEN", "LGEN.L");
        SYMBOL_MAP.put("NG.", "NG.L");
        SYMBOL_MAP.put("RDSB", "RDSB.L");
        SYMBOL_MAP.put("SGE", "SGE.L");
        SYMBOL_MAP.put("ULVR", "ULVR.L");
        SYMBOL_MAP.put("WLDS", "WLDS.L");

        // todo .. add more client to service space symbol mappings as appropriate!
    }

    @Override
    public List<DatedQuotes> getStockQuotes(List<String> symbols, LocalDate startDate, LocalDate endDate, QuoteType type, final List<String> failed) {
        final List<String> mappedSymbols = mapSymbols(symbols);
        switch (type) {
            case EOD:
                break;
            case EOM:
                break;
        }

        final List<DatedQuotes> dateQuotes = new ArrayList<>();

        final String function = type.equals(QuoteType.EOD) ? DAILY_ADJUSTED_STOCK_QUOTE : MONTHLY_ADJ_STOCK_QUOTE;

        HttpURLConnection con = null;
        //
        boolean hasError = false;
        final Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})|(\\d+\\.*\\d+)"); //tested at https://regex-testdrive.com/en/dotest with 2020-09-17,202.8500,205.5800,202.0000,205.2700,205.2700,7420687,0.0000,1.0000
        final Matcher matcher = pattern.matcher("");

        final List<DatedQuote> records = new ArrayList<>();

        for (String symbol : mappedSymbols) {
            try {

                //API is limited to 5 reqs per minute, in theory. So 12secs between requests is required. We use a random range between 15 to 30 secs
                final long delay = (long) ((Math.random() * 15000) + 15000);
                System.out.println(String.format("Processing  ::: '%s'", symbol));
                Thread.sleep(delay);

                final String requestURL = String.format(PRICE_CSV_URL_TEMPLATE, function, symbol, API_KEY); // Another option is to use RestTemplates from Spring   
                URL myurl = new URL(requestURL);
                con = (HttpURLConnection) myurl.openConnection();
                con.setRequestProperty(UA, UA_VALUE); // Do as if you're using Chrome 41 on Windows 7.
                con.setRequestMethod(GET_METHOD);
                con.setRequestProperty(ACCEPT_CHARSET, ACCEPT_CHARSET_VALUE);

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //sc = new Scanner(con.getInputStream());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        final String firstLine = reader.readLine().trim();
                        if (firstLine.equals(API_ERROR_PREFIX)) {
                            //hasError = true;
                            //break;
                            System.out.println(String.format("Error downloading data for symbol::: '%s'", symbol));
                            failed.add(symbol);
                        } else {
                            String line;
                            //Scanner sc = null;
                            LocalDate lineDate = null;
                            while ((line = reader.readLine()) != null) {
                                int index = 0;
                                matcher.reset(line);
                                while (matcher.find()) {
                                    if (index == 0) {
                                        //read date
                                        lineDate = LocalDate.parse(matcher.group());
                                        // see if this date is relevant
                                        if (lineDate.isBefore(startDate) || lineDate.isAfter(endDate)) {
                                            break;
                                        }
                                    } else if (index == 5) {
                                        //adjusted close
                                        final DatedQuote q = new DatedQuote(lineDate, symbol, Double.parseDouble(matcher.group()));
                                        records.add(q);
                                        //System.out.println(String.format("%s :: %s :: %s", lineDate, symbol, q.getValue()));
                                        break;
                                    }
                                    index++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        hasError = true;
                        break;
                    }
                }
            } catch (Exception e) {
                hasError = true;
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }
        if (hasError == false) {
            records.stream().collect(Collectors.groupingBy(DatedQuote::getDate)).forEach((k, v) -> {
                final DatedQuotes dq = new DatedQuotes();
                final List<Quote> oneDateQuotes = v.stream().map(h -> new Quote(h.getSymbol(), h.getValue())).collect(Collectors.toList());
                final LocalDate date = v.get(0).getDate();
                dq.setDate(date);
                dq.setStockQuotes(oneDateQuotes);
                dateQuotes.add(dq);
            });
            dateQuotes.sort(Comparator.comparing(DatedQuotes::getDate));
        }
        return dateQuotes;
    }

    @Override
    public List<DatedQuotes> getFXQuotes(List<Currency> currencies, LocalDate startDate, LocalDate endDate, QuoteType type, List<String> failed) {
        final List<DatedQuotes> dateQuotes = new ArrayList<>();

        switch (type) {
            case EOD:
                break;
            case EOM:
                break;
        }

        final String function = type.equals(QuoteType.EOD) ? DAILY_FX_QUOTE : MONTHLY_FX_QUOTE;

        HttpURLConnection con = null;
        //
        boolean hasError = false;
        final Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})|(\\d+\\.*\\d+)"); //tested at https://regex-testdrive.com/en/dotest with 2020-09-17,202.8500,205.5800,202.0000,205.2700,205.2700,7420687,0.0000,1.0000
        final Matcher matcher = pattern.matcher("");

        final List<DatedFXQuote> records = new ArrayList<>();

        final List<String> resolvedPairs = new ArrayList<>();

        for (final Currency fromCurrency : currencies) {
            if (fromCurrency.getCode().endsWith("X") == false) {
                final int fromCurrencyIndex = currencies.indexOf(fromCurrency);
                for (final Currency toCurrency : currencies) {
                    final String fxSymbol = String.format("%s%s", fromCurrency.getCode(), toCurrency.getCode());
                    if (resolvedPairs.contains(fxSymbol) == false && toCurrency.getCode().endsWith("X") == false) {
                        //uniquePairs.add(new Currency[]{fromCurrency, toCurrency});
                        final int toCurrencyIndex = currencies.indexOf(toCurrency);
                        try {
                            //API is limited to 5 reqs per minute, in theory. So 12secs between requests is required. We use a random range between 15 to 30 secs
                            final long delay = (long) ((Math.random() * 15000) + 15000);
                            System.out.println(String.format("Processing  ::: '%s'", fxSymbol));
                            Thread.sleep(delay);

                            final String requestURL = String.format(FX_CSV_URL_TEMPLATE, function, fromCurrency.getCode(), toCurrency.getCode(), API_KEY); // Another option is to use RestTemplates from Spring   
                            URL myurl = new URL(requestURL);
                            con = (HttpURLConnection) myurl.openConnection();
                            con.setRequestProperty(UA, UA_VALUE); // Do as if you're using Chrome 41 on Windows 7.
                            con.setRequestMethod(GET_METHOD);
                            con.setRequestProperty(ACCEPT_CHARSET, ACCEPT_CHARSET_VALUE);

                            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                                    final String firstLine = reader.readLine().trim();
                                    if (firstLine.equals(API_ERROR_PREFIX)) {
                                        //hasError = true;
                                        //break;
                                        System.out.println(String.format("Error downloading data for symbol::: '%s'", fxSymbol));
                                        failed.add(fxSymbol);
                                    } else {
                                        String line;
                                        LocalDate lineDate = null;
                                        int readLines = 0;
                                        while ((line = reader.readLine()) != null) {
                                            int index = 0;
                                            matcher.reset(line);
                                            while (matcher.find()) {
                                                if (index == 0) {
                                                    //read date
                                                    lineDate = LocalDate.parse(matcher.group());
                                                    // see if this date is relevant
                                                    if (lineDate.isBefore(startDate) || lineDate.isAfter(endDate)) {
                                                        break;
                                                    }
                                                } else if (index == 4) {
                                                    //fx close
                                                    final double aaabbb = Double.parseDouble(matcher.group());
                                                    //AAABBB
                                                    records.add(new DatedFXQuote(lineDate, fromCurrency.getId(), toCurrency.getId(), fxSymbol, aaabbb));
                                                    //AAABBX
                                                    records.add(new DatedFXQuote(lineDate, fromCurrency.getId(), toCurrency.getId() + 1, fromCurrency.getCode() + currencies.get(toCurrencyIndex + 1).getCode(), 100 * aaabbb));
                                                    //
                                                    //AAXBBB
                                                    records.add(new DatedFXQuote(lineDate, fromCurrency.getId() + 1, toCurrency.getId(), currencies.get(fromCurrencyIndex + 1).getCode() + toCurrency.getCode(), aaabbb / 100.0));
                                                    //AAXBBX
                                                    records.add(new DatedFXQuote(lineDate, fromCurrency.getId() + 1, toCurrency.getId() + 1, currencies.get(fromCurrencyIndex + 1).getCode() + currencies.get(toCurrencyIndex + 1).getCode(), aaabbb));

                                                    if (readLines == 0) {//do onliy when the first dataline in the csv is encountered
                                                        resolvedPairs.add(fxSymbol);//mark this main pair as resolved
                                                    }

                                                    if (fromCurrency.getId() != toCurrency.getId()) {
                                                        //get associated pair quotes and mark as reolved too. 
                                                        //E.g if we just retrieved USDGBP above, we would have calculated USDGBP, USDGBX, USXGBP, USXGBX. 
                                                        //But we can also right now have information to compute GBPUSD (the associated main pair) and derivatives e.g GBPUSD, GBXUSD,GBXUSX

                                                        final String fxSymbol2 = String.format("%s%s", toCurrency.getCode(), fromCurrency.getCode());

                                                        if (readLines == 0) {//do onliy when the first dataline in the csv is encountered
                                                            resolvedPairs.add(fxSymbol2);//mark tthe associated main pair as resolved so that it is not recomputed
                                                            System.out.println(String.format("\tCalculating  ::: '%s'", fxSymbol2));
                                                        }

                                                        final double bbbaaa = 1.0 / aaabbb;

                                                        //BBBAAA
                                                        records.add(new DatedFXQuote(lineDate, toCurrency.getId(), fromCurrency.getId(), fxSymbol2, bbbaaa));
                                                        //BBBAAX
                                                        records.add(new DatedFXQuote(lineDate, toCurrency.getId(), fromCurrency.getId() + 1, currencies.get(toCurrencyIndex).getCode() + currencies.get(fromCurrencyIndex + 1).getCode(), 100 * bbbaaa));
                                                        //
                                                        //BBXAAA
                                                        records.add(new DatedFXQuote(lineDate, toCurrency.getId() + 1, fromCurrency.getId(), currencies.get(toCurrencyIndex + 1).getCode() + currencies.get(fromCurrencyIndex).getCode(), bbbaaa / 100.0));
                                                        //BBXAAX
                                                        records.add(new DatedFXQuote(lineDate, toCurrency.getId() + 1, fromCurrency.getId() + 1, currencies.get(toCurrencyIndex + 1).getCode() + currencies.get(fromCurrencyIndex + 1).getCode(), bbbaaa));
                                                    }
                                                    break;
                                                }
                                                index++;
                                            }
                                            readLines++;
                                        }
                                    }
                                } catch (Exception e) {
                                    hasError = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            hasError = true;
                        } finally {
                            if (con != null) {
                                con.disconnect();
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Resolved FX pairs:::");
        resolvedPairs.stream().forEach(s -> System.out.print(String.format("%s |", s)));

        if (hasError == false) {
            records.stream().collect(Collectors.groupingBy(DatedFXQuote::getDate)).forEach((k, v) -> {
                final DatedQuotes dq = new DatedQuotes();
                final List<FXQuote> oneDateQuotes = v.stream().map(h -> new FXQuote(h.getFrom(), h.getTo(), h.getSymbol(), h.getValue())).collect(Collectors.toList());
                final LocalDate date = v.get(0).getDate();
                dq.setDate(date);
                dq.setFxQuotes(oneDateQuotes);
                dateQuotes.add(dq);
            });
            dateQuotes.sort(Comparator.comparing(DatedQuotes::getDate));
        }

        return dateQuotes;
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
    }

}
