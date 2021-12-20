package org.halext.deltaseeker.service.data;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Order {

    private final SimpleStringProperty symbol;
    private final SimpleStringProperty assetType;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty orderType;
    private final SimpleDoubleProperty quantity;
    private final SimpleStringProperty enteredTime;

    public Order(String symbol, String assetType, 
                 Double price, String orderType, Double quantity,
                 String enteredTime) {
        this.symbol = new SimpleStringProperty(symbol);
        this.assetType = new SimpleStringProperty(assetType);
        this.price = new SimpleDoubleProperty(price);
        this.orderType = new SimpleStringProperty(orderType);
        this.quantity = new SimpleDoubleProperty(quantity);
        this.enteredTime = new SimpleStringProperty(enteredTime);
    }

    public String getSymbol() {
        return symbol.get();
    }

    public String getAssetType() {
        return assetType.get();
    }

    public Double getPrice() {
        return price.get();
    }

    public Double getQuantity() {
        return quantity.get();
    }

    public String getEnteredTime() {
        return enteredTime.get();
    }

    public String getOrderType() {
        return orderType.get();
    }
}
