/*
 * 2020.09.18  - Created
 */
package mx4.springboot.model;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Ilamah, Osho
 */
@Document(collection = "quote")
public class DateQuotes {
    private List<Quote> quotes;
    private LocalDate date;

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }   
}
