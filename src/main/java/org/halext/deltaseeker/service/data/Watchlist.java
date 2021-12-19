package org.halext.deltaseeker.service.data;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Watchlist {

    public class Symbol {
        private final SimpleStringProperty symbol;

        public Symbol(String s) {
            this.symbol = new SimpleStringProperty(s);
        }

        public String getSymbol() {
            return symbol.get();
        }
    }

    private String name;
    private String watchlistId;
    private ArrayList<Symbol> symbols;
    private HashMap<String, Object> itemList;

    public Watchlist(String name, String id) {
        symbols = new ArrayList<>();
        itemList = new HashMap<>();
        this.name = name;
        this.watchlistId = id;
    }

    public void addItem(String key, Object value) {
        itemList.put(key, value);
    }

    public Object getValue(String key) {
        return itemList.get(key);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return watchlistId;
    }

    public ObservableList<Symbol> createSymbolList() {
        return FXCollections.observableArrayList(symbols);
    }

    public void addSymbol(String ticker) {
        symbols.add(new Symbol(ticker));
    }

}
