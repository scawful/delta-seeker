package org.halext.deltaseeker.service.data;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;

public class Instrument {

    private HashMap<String, Object> fundamentalData = new HashMap<>();

    private final String[] instrumentTypes = { 
        "EQUITY", 
        "ETF", 
        "FOREX", 
        "FUTURE", 
        "FUTURE_OPTION", 
        "INDEX", 
        "INDICATOR", 
        "MUTUAL_FUND", 
        "OPTION", "UNKNOWN"
    };

    private SimpleStringProperty cusip;
    private SimpleStringProperty symbol;
    private SimpleStringProperty description;
    private SimpleStringProperty exchange;
    private SimpleStringProperty assetType; 
    private SimpleStringProperty cashType;
    private SimpleStringProperty optionType;
    private SimpleStringProperty putCall;
    private SimpleStringProperty underlyingSymbol;

    public String[] getInstrumentTypes() {
        return this.instrumentTypes;
    }

    public String getUnderlyingSymbol() {
        return this.underlyingSymbol.get();
    }

    public void setUnderlyingSymboll(String us) {
        this.underlyingSymbol = new SimpleStringProperty(us);
    }

    public String getPutCall() {
        return this.putCall.get();
    }

    public void setPutCall(String pc) {
        this.putCall = new SimpleStringProperty(pc);
    }

    public String getOptionType() {
        return this.optionType.get();
    }

    public void setOptionType(String type) {
        this.optionType = new SimpleStringProperty(type);
    }

    public String getCashType() {
        return this.cashType.get();
    }

    public void setCashType(String type) {
        this.cashType = new SimpleStringProperty(type);
    }

    public String getCusip() {
        return this.cusip.get();
    }

    public void setCusip(String cusip) {
        this.cusip = new SimpleStringProperty(cusip);
    }

    public String getSymbol() {
        return this.symbol.get();
    }

    public void setSymbol(String s) {
        symbol = new SimpleStringProperty(s);
    }

    public String getDescription() {
        return this.description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public String getExchange() {
        return this.exchange.get();
    }

    public void setExchange(String exchange) {
        this.exchange = new SimpleStringProperty(exchange);
    }

    public String getAssetType() {
        return this.assetType.get();
    }

    public void setAssetType(String assetType) {
        this.assetType = new SimpleStringProperty(assetType);
    }

    public HashMap<String,Object> getFundamentalData() {
        return this.fundamentalData;
    }

    public void setFundamentalData(HashMap<String,Object> fundamentalData) {
        this.fundamentalData = fundamentalData;
    }

    public void insertFundamental(String key, Object object) {
        fundamentalData.put(key, object);
    }

    public Object getFundamental(String key) {
        return fundamentalData.get(key);
    }

}
