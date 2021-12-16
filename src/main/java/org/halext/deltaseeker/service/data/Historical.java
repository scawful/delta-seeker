package org.halext.deltaseeker.service.data;

import java.util.ArrayList;
import java.util.Date;

public class Historical {
    
    private static class Candle {
        int volume;
        double high;
        double low;
        double open;
        double close;
        Date datetime;

        Candle(int v, double h, double l, double o, double c, String dt) {
            this.volume = v;
            this.high = h;
            this.low = l;
            this.open = o;
            this.close = c;

        }
    }

    static ArrayList<Candle> candles;

    public Historical() {

    }

    public static void addCandle(int v, double h, double l, double o, double c, String dt) {
        Candle new_candle = new Candle(v,h,l,o,c,dt);
        candles.add(new_candle);
    }


}
