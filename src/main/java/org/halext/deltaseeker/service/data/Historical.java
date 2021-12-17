package org.halext.deltaseeker.service.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Historical {
    
    public static class Candle {
        long volume;
        public double high;
        public double low;
        public double open;
        public double close;
        Long rawDatetime;
        public Date datetime;

        Candle(long v, double h, double l, double o, double c, Long rdt, Date dt) {
            this.volume = v;
            this.high = h;
            this.low = l;
            this.open = o;
            this.close = c;
            this.datetime = dt;
            this.rawDatetime = rdt;
        }

    }

    public static class SortCandle implements Comparator<Candle> {
 
        // Method
        // Sorting in ascending order
        public int compare(Candle a, Candle b)
        {
            return a.rawDatetime.compareTo(b.rawDatetime);
        }
    }

    public static ArrayList<Candle> candles;

    public Historical() {
        
    }

    public static void addCandle(long v, double h, double l, double o, double c, Long rdt, Date dt) {
        if ( candles == null ) {
            candles = new ArrayList<Candle>();
        }
        
        Candle new_candle = new Candle(v,h,l,o,c,rdt,dt);
        candles.add(new_candle);
    }

    public static void sortCandles() {
        Collections.sort(candles, new SortCandle());
    }

    public static void printCandles() {
        for ( int i = 0; i < candles.size(); i++ ) {
            System.out.println(candles.get(i).open + " / " + candles.get(i).close + " - " + candles.get(i).datetime);
        }
    }


}
