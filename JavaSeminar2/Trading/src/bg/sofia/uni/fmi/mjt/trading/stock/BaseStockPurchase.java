package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public abstract class BaseStockPurchase implements StockPurchase {
    private int quantity;
    private LocalDateTime purchaseTimestamp;
    private double purchasePricePerUnit;

    public BaseStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit) {
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        this.purchasePricePerUnit = purchasePricePerUnit;
    }

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
}
