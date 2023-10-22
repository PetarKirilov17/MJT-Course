package bg.sofia.uni.fmi.mjt.trading.stock;

import bg.sofia.uni.fmi.mjt.trading.Constants;
import java.time.LocalDateTime;

public class MicrosoftStockPurchase extends BaseStockPurchase{

    public MicrosoftStockPurchase(String ticker, int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        setTicker(ticker);
        super.quantity = quantity;
        super.purchaseTimestamp = purchaseTimestamp;
        super.purchasePricePerUnit = purchasePricePerUnit;
    }
    private void setTicker(String ticker){
        if(ticker.equals(Constants.MICROSOFT_TICKER)){
            this.ticker = ticker;
        }
        else {
            this.ticker = "";
        }
    }
}
