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

    public Instrument() {

    }

    public String[] getInstrumentTypes() {
        return this.instrumentTypes;
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
