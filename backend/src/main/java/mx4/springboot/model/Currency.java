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

}
