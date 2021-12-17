package org.halext.deltaseeker.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.Date;
import java.util.Iterator;

import org.halext.deltaseeker.service.data.Historical;

public class Parser {
    
    public static void parsePriceHistory( JSONObject jo ) {
        JSONArray candles = (JSONArray) jo.get("candles");

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> it = candles.iterator();
		while (it.hasNext()) {
            JSONObject candle = (JSONObject) it.next();
            long volume = (long) candle.get("volume");
            double high = (double) candle.get("high");
            double low = (double) candle.get("low");
            double open = (double) candle.get("open");
            double close = (double) candle.get("close");
            Long rawDatetime = (Long) candle.get("datetime");
            Date datetime = new java.util.Date((long) candle.get("datetime") );
            System.out.println(datetime.toString());
            Historical.addCandle(volume, high, low, open, close, rawDatetime, datetime);
		}   
        
        Historical.sortCandles();
        Historical.printCandles();
    }
}
