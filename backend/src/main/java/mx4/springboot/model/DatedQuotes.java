/*
 * 2020.09.18  - Created
 */
package mx4.springboot.model;

import java.time.LocalDate;
import java.util.List;
import mx4.springboot.model.Quote.FXQuote;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Ilamah, Osho
 */
@Document(collection = "quote")
public class DatedQuotes {
    
    private String id;

    private List<Quote> stockQuotes;
    private List<FXQuote> fxQuotes;
    private LocalDate date;

    public List<Quote> getStockQuotes() {
        return stockQuotes;
    }

    public void setStockQuotes(List<Quote> stockQuotes) {
        this.stockQuotes = stockQuotes;
    }

    public List<FXQuote> getFxQuotes() {
        return fxQuotes;
    }

    public void setFxQuotes(List<FXQuote> fxQuotes) {
        this.fxQuotes = fxQuotes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}
