package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public abstract class BaseStockPurchase implements StockPurchase {

    protected String ticker;
    protected int quantity;
    protected LocalDateTime purchaseTimestamp;
    protected double purchasePricePerUnit;
    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return (double) Math.round(this.purchasePricePerUnit * 100) / 100;
    }

    @Override
    public double getTotalPurchasePrice() {
        double result = getQuantity() * getPurchasePricePerUnit();
        return (double) Math.round(result * 100) / 100;
    }

    @Override
    public String getStockTicker() {
        return ticker;
    }
}
