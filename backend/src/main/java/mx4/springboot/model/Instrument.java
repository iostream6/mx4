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
    private long currencyId;

    /**
     * A string code that represents this instrument. Example : MSFT | USDCASH | etc.
     */
    private String code;

    private String id;

    /**
     * A String identifier for the company to which this instrument is associated. A single company can have multiple instruments (e.g. RDSB, RDSA; or listing in NYSE versus another in LSE, etc).
     */
    private String entityId;

    /**
     * A String description for this instrument.
     */
    private String description;
    
    private String countryId;
    
    private String sectorId;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getSectorId() {
        return sectorId;
    }

    public void setSectorId(String sectorId) {
        this.sectorId = sectorId;
    }


    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static enum Type {
        STOCK, CASH, BOND, DEBT
    }
}
