package org.halext.deltaseeker.service.data;

import java.util.HashMap;

public class Instrument {

    private String[] instrumentTypes = { 
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

    private static String cusip;
    private static String symbol;
    private static String description;
    private static String exchange;
    private static String assetType; 

    private static HashMap<String, Object> fundamentalData = new HashMap<>();

    private Instrument() {

    }

    public static void setSymbol(String s) {
        symbol = s;
    }

    public static void insertFundamental(String key, Object object) {
        fundamentalData.put(key, object);
    }

}
