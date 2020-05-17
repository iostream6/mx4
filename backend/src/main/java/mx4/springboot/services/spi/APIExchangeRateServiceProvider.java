/*
 * 2020.04.03  - Created
 */
package mx4.springboot.services.spi;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mx4.springboot.model.Currency;
import mx4.springboot.model.Currency.Exchange;
import org.openide.util.lookup.ServiceProvider;

/**
 * Exchange rate service provider based on exchangerates.io API service.
 *
 * @author Ilamah, Osho
 */
@ServiceProvider(service = ExchangeRateServiceProvider.class, position = 10)
public class APIExchangeRateServiceProvider implements ExchangeRateServiceProvider {
//https://api.exchangeratesapi.io/latest?base=USD&symbols=USD,GBP
//"https://api.exchangeratesapi.io/latest?";
//https://api.exchangeratesapi.io/history?base=EUR&start_at=2020-04-02&end_at=2020-04-02&symbols=USD,GBP,EUR
//https://api.exchangeratesapi.io/latest?base=GBP
    private final static String REQUEST_URL_TEMPLATE = "https://api.exchangeratesapi.io/latest?base=%s", METHOD = "GET";
    private static final String UA = "User-Agent", ACCEPT_CHARSET = "Accept-Charset", UA_VALUE = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    private static final String ACCEPT_CHARSET_VALUE = java.nio.charset.StandardCharsets.UTF_8.name();

    @Override //OK
    public List<Exchange> fetchExchangeRates(List<Currency> currencies) {
        final List<Exchange> exchanges = new ArrayList<>();
        for (Currency c : currencies) {
            if (c.getCode().length() == 3) {
                HttpURLConnection con = null;
                try {
                    
                    // Another option is to use RestTemplates from Spring
                    
                    final String url = String.format(REQUEST_URL_TEMPLATE, c.getCode());
                    URL myurl = new URL(url);
                    con = (HttpURLConnection) myurl.openConnection();
                    con.setRequestProperty(UA, UA_VALUE); // Do as if you're using Chrome 41 on Windows 7.
                    con.setRequestMethod(METHOD);
                    con.setRequestProperty(ACCEPT_CHARSET, ACCEPT_CHARSET_VALUE);

                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        ObjectMapper mapper = new ObjectMapper();
                        FixerExchangeInfo exchangeInfo = mapper.readValue(con.getInputStream(), FixerExchangeInfo.class);
                        if (exchangeInfo.getRates().isEmpty() == false) {
                            currencies.stream().filter(cc -> /*cc.getCurrencyID() != c.getCurrencyID() &&*/ cc.getCode().length() == 3).forEach(ccc -> {
                                Double forwardRate = exchangeInfo.getRates().get(ccc.getCode());
                                if (forwardRate != null) {
                                    Exchange ex = new Exchange();
                                    ex.setDatestamp(exchangeInfo.getTimestamp());
                                    ex.setFromID(c.getCurrencyID());
                                    ex.setToID(ccc.getCurrencyID());
                                    ex.setConverter(forwardRate);
                                    exchanges.add(ex);
                                    //
                                    //fractionals
                                    ex = new Exchange();
                                    ex.setDatestamp(exchangeInfo.getTimestamp());
                                    ex.setFromID(c.getCurrencyID());
                                    ex.setToID(ccc.getCurrencyID() + 1);
                                    ex.setConverter(100 * forwardRate);
                                    exchanges.add(ex);
                                }

                            });
                        }
                    }

                } catch (Exception e) {
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }

                }
            }
        }

        return exchanges;
    }
    //
    //DTO that maps to exchangeratesapi.io "latest" results JSON :: {"rates":{"CAD": 1.4167430772,"HKD": 7.7521547772,"ISK": 142.3986796259, . . . "PLN": 4.1900788557},"base": "USD","date": "2020-04-02"}
    // very similar to Fixer.io JSON format except that "success" and timestamp are not present (Fixer.io :: {"success":true,"timestamp":1585904948,"base":"EUR","date":"2020-04-03","rates":{"USD":1.079436,"GBP":0.879541,"NGN":396.153627}}
    public static class FixerExchangeInfo {

        private boolean success;
        private long timestamp;
        private String base;
        private String date;
        private Map<String, Double> rates;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Map<String, Double> getRates() {
            return rates;
        }

        public void setRates(Map<String, Double> rates) {
            this.rates = rates;
        }

    }

}
