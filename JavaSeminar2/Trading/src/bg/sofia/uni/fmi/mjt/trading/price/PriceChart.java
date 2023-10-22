package bg.sofia.uni.fmi.mjt.trading.price;
import bg.sofia.uni.fmi.mjt.trading.Constants;

public class PriceChart implements PriceChartAPI {
    private double microsoftStockPrice;
    private double amazonStockPrice;
    private double googleStockPrice;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice){
        setMicrosoftStockPrice(microsoftStockPrice);
        setGoogleStockPrice(googleStockPrice);
        setAmazonStockPrice(amazonStockPrice);
    }

    @Override
    public double getCurrentPrice(String stockTicker) {
        double result = 0.0;
        switch (stockTicker){
            case Constants.MICROSOFT_TICKER -> result = microsoftStockPrice;
            case Constants.AMAZON_TICKER ->result = amazonStockPrice;
            case Constants.GOOGLE_TICKER -> result = googleStockPrice;
            default -> result = 0.0;
        }
        return (double)Math.round(result * 100) / 100;
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if(percentChange <= 0){
            return false;
        }
        if(stockTicker == null){
            return false;
        }
        switch (stockTicker){
            case Constants.MICROSOFT_TICKER -> {
                setMicrosoftStockPrice(microsoftStockPrice + (percentChange/(double)100)*microsoftStockPrice);
                return true;
            }
            case Constants.AMAZON_TICKER -> {
                setAmazonStockPrice(amazonStockPrice + (percentChange/(double)100)*amazonStockPrice);
                return true;
            }
            case Constants.GOOGLE_TICKER -> {
                setGoogleStockPrice(googleStockPrice + (percentChange/(double)100)*googleStockPrice);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
    private boolean validatePrice(double price){
        return price >= 0;
    }

    private void setMicrosoftStockPrice(double microsoftStockPrice){
        if(validatePrice(microsoftStockPrice)){
            this.microsoftStockPrice = microsoftStockPrice;
        }
    }

    private void setAmazonStockPrice(double amazonStockPrice){
        if(validatePrice(amazonStockPrice)){
            this.amazonStockPrice = amazonStockPrice;
        }
    }

    private void setGoogleStockPrice(double googleStockPrice){
        if(validatePrice(googleStockPrice)){
            this.googleStockPrice = googleStockPrice;
        }
    }
}
