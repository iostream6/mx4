/*
 * 2020.09.18  - Created
 */
package mx4.springboot.model;

/**
 * A basic data model for a quote. Not intended for persistence (no id).
 *
 * @author Ilamah, Osho
 */
public class Quote {

    private String code;// for stocks this will be the instrument ID, for fx, it will be the AAABBB string
    private double value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Quote() {
    }

    public Quote(String code, double value) {
        this.code = code;
        this.value = value;
    }

    public static class FXQuote extends Quote {

        public FXQuote() {
        }

        public FXQuote(long from, long to, String code, double value) {
            super(code, value);
            this.from = from;
            this.to = to;
        }

        private long from;

        private long to;

        public long getFrom() {
            return from;
        }

        public void setFrom(long from) {
            this.from = from;
        }

        public long getTo() {
            return to;
        }

        public void setTo(long to) {
            this.to = to;
        }

    }
}
