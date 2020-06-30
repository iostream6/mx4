/*
 * 2020.06.17 - Created
 * 2020.06.29 - Switch to LocalDate rather dates
 */
package mx4.springboot.model;

import java.time.LocalDate;
import java.util.Set;

/**
 * Records of the values (e.g. prices) of one or more instruments. Useful for portfolio growth calculations. Avoids the need to stream values via APIs. Idea is to save these values sparsely (say every
 * quarter) to avoid the need to have daily or more regular values data in a DB.
 *
 * @author Ilamah, Osho
 */
public class Values {

    private LocalDate date;
    private String id;
    private Set<Record> records;

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

    public Set<Record> getRecords() {
        return records;
    }

    public void setRecords(Set<Record> records) {
        this.records = records;
    }

    public static class Record {

        private String instrumentId;
        private double value;

        public String getInstrumentId() {
            return instrumentId;
        }

        public void setInstrumentId(String instrumentId) {
            this.instrumentId = instrumentId;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

    }
}
