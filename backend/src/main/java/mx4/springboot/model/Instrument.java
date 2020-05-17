/*
 * 2020.03.31  - Created
 */
package mx4.springboot.model;

/**
 *
 * @author Ilamah, Osho
 */
public class Instrument {

    /**
     * The type of this instrument.
     */
    private Type type;

    /**
     * A long identifier for this instruments currency.
     */
    private long currencyID;

    /**
     * A string code that represents this instrument. Example : MSFT | USDCASH | etc.
     */
    private String code;

    /**
     * A long identifier for this instrument.
     */
    private long instrumentID;

    /**
     * A long identifier for the company to which this instrument is associated. A single company can have multiple instruments (e.g. RDSB, RDSA; or listing in NYSE versus another in LSE, etc).
     */
    private long companyID;

    /**
     * A String representation of the Bloomberg search string for this instrument, if any.
     */
    private String bloombergQuoteString;

    /**
     * A String representation of the Google search string for this instrument, if any.
     */
    private String googleQuoteString;

    /**
     * A String representation of the FT search string for this instrument, if any.
     */
    private String ftQuoteString;

    /**
     * A String description for this instrument.
     */
    private String description;
    
    private long countryID;
    
    private long sectorID;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(long currencyID) {
        this.currencyID = currencyID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(long instrumentID) {
        this.instrumentID = instrumentID;
    }

    public long getCompanyID() {
        return companyID;
    }

    public void setCompanyID(long companyID) {
        this.companyID = companyID;
    }

    public String getBloombergQuoteString() {
        return bloombergQuoteString;
    }

    public void setBloombergQuoteString(String bloombergQuoteString) {
        this.bloombergQuoteString = bloombergQuoteString;
    }

    public String getGoogleQuoteString() {
        return googleQuoteString;
    }

    public void setGoogleQuoteString(String googleQuoteString) {
        this.googleQuoteString = googleQuoteString;
    }

    public String getFtQuoteString() {
        return ftQuoteString;
    }

    public void setFtQuoteString(String ftQuoteString) {
        this.ftQuoteString = ftQuoteString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCountryID() {
        return countryID;
    }

    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public long getSectorID() {
        return sectorID;
    }

    public void setSectorID(long sectorID) {
        this.sectorID = sectorID;
    }

    public static enum Type {
        STOCK, CASH, BOND, DEBT
    }
}
