/*
 * 2020.03.31  - Created
 */
package mx4.springboot.model;

/**
 *
 * @author Ilamah, Osho
 */
public class Currency {

    /**
     * A string symbol that represents this currency. Example : $
     */
    private String symbol;

    /**
     * A short alphabetic code to represent this currency. Example : USD
     */
    private String code;

    /**
     * The formal name for this currency. Examples include Dollar, Pounds, etc.
     */
    private String name;

    /**
     * A long identifier for this currency
     */
    private long id;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static class Exchange {

        private String id;//echange ID
        
        private long fromID;

        private long toID;

        private double converter;

        private long datestamp;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        public long getFromID() {
            return fromID;
        }

        public void setFromID(long fromID) {
            this.fromID = fromID;
        }

        public long getToID() {
            return toID;
        }

        public void setToID(long toID) {
            this.toID = toID;
        }

        public double getConverter() {
            return converter;
        }

        public void setConverter(double converter) {
            this.converter = converter;
        }

        public long getDatestamp() {
            return datestamp;
        }

        public void setDatestamp(long datestamp) {
            this.datestamp = datestamp;
        }

    }

}
