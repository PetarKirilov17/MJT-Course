package bg.sofia.uni.fmi.mjt.trading.stock;
import bg.sofia.uni.fmi.mjt.trading.Constants;
import java.time.LocalDateTime;

public class AmazonStockPurchase extends BaseStockPurchase{
    public AmazonStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        super(quantity, purchaseTimestamp, purchasePricePerUnit);
    }

    @Override
    public String getStockTicker() {
        return Constants.AMAZON_TICKER;
    }
}
