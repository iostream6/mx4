/*
 * 2020.03.31  - Created
 * 2020.07.04  - Switch transaction date to LocalDate from Date 
 */
package mx4.springboot.model;

import java.time.LocalDate;

/**
 *
 * @author Ilamah, Osho
 */
public class Transaction {

    private String id;

    private LocalDate date;

    private String portfolioId;

    private int currencyId;

    private String instrumentId;

    private double units;

    private double amountPerUnit;

    private double fees;

    private double taxes;

    private Type type;
    
    private boolean provisional;

    public boolean isProvisional() {
        return provisional;
    }

    public void setProvisional(boolean provisional) {
        this.provisional = provisional;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getAmountPerUnit() {
        return amountPerUnit;
    }

    public void setAmountPerUnit(double amountPerUnit) {
        this.amountPerUnit = amountPerUnit;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        BUY, SELL, DIV, SDIV, ROC
    }
}
