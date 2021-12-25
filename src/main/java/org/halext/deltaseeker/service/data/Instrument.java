package org.halext.deltaseeker.service.data;

import java.util.HashMap;

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

    private String cusip;
    private String symbol;
    private String description;
    private String exchange;
    private String assetType; 

    
    public Instrument() {

    }

    public String[] getInstrumentTypes() {
        return this.instrumentTypes;
    }

    public String getCusip() {
        return this.cusip;
    }

    public void setCusip(String cusip) {
        this.cusip = cusip;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String s) {
        symbol = s;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExchange() {
        return this.exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getAssetType() {
        return this.assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
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
