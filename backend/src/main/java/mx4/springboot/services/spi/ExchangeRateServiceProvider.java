/*
 * 2020.04.03  - Created
 */
package mx4.springboot.services.spi;

import java.util.List;
import mx4.springboot.model.Currency;
import mx4.springboot.model.Currency.Exchange;


/**
 *
 * @author Ilamah, Osho
 */
public interface ExchangeRateServiceProvider {

    List<Exchange> fetchExchangeRates(List<Currency> currencies);
}
