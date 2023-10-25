package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChart;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;
import bg.sofia.uni.fmi.mjt.trading.Constants;

import java.time.LocalDateTime;

public class Portfolio implements PortfolioAPI{
    private String owner;
    private PriceChartAPI priceChart;
    private StockPurchase[] stockPurchases;
    private double budget;
    private int maxSize;
    private int currentIndex;

    public Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.maxSize = maxSize;
        this.stockPurchases = new StockPurchase[maxSize];
        currentIndex = 0;
    }

    public Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.maxSize = maxSize;
        this.stockPurchases = new StockPurchase[maxSize];
        currentIndex = stockPurchases.length;
        System.arraycopy(stockPurchases, 0, this.stockPurchases, 0, stockPurchases.length);
    }
    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if(quantity <= 0){
            return null;
        }
        if(stockTicker == null){
            return null;
        }
        if(currentIndex >= maxSize){
            return null;
        }
        StockPurchase purchase;
        switch (stockTicker){
            case Constants.MICROSOFT_TICKER -> {
                purchase = new MicrosoftStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            }
            case Constants.AMAZON_TICKER -> {
                purchase = new AmazonStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            }
            case Constants.GOOGLE_TICKER -> {
                purchase = new GoogleStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            }
            default -> {
                return null;
            }
        }
        if(Double.compare(budget, (purchase.getPurchasePricePerUnit() * quantity)) < 0){ // budget not enough
            return null;
        }

        budget -= purchase.getPurchasePricePerUnit()*quantity; // decrease the budget
        priceChart.changeStockPrice(stockTicker, 5); // change the price of the stock
        stockPurchases[currentIndex++] = purchase; // add the purchase
        return purchase;
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        StockPurchase[] stockPurchasesResult = new StockPurchase[currentIndex];
        for (int i =0; i < currentIndex; i++){
            stockPurchasesResult[i] = stockPurchases[i];
        }
        return stockPurchasesResult;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        //find count
        int countOfPurchasesInInterval = 0;
        for (int i = 0; i < currentIndex; i++){
            var sp = stockPurchases[i];
            if(!sp.getPurchaseTimestamp().isBefore(startTimestamp) && !sp.getPurchaseTimestamp().isAfter(endTimestamp)){
                countOfPurchasesInInterval++;
            }
        }

        StockPurchase[] result = new StockPurchase[countOfPurchasesInInterval];
        int resultIndex = 0;
        for(int i = 0; i < currentIndex; i++){
            var sp = stockPurchases[i];
            if(!sp.getPurchaseTimestamp().isBefore(startTimestamp) && !sp.getPurchaseTimestamp().isAfter(endTimestamp)){
                result[resultIndex++] = sp;
            }
        }
        return result;
    }
    @Override
    public double getNetWorth() {
        double sum = 0.0;
        for (int i = 0; i < currentIndex; i++){
            var sp = stockPurchases[i];
            sum+=priceChart.getCurrentPrice(sp.getStockTicker()) * sp.getQuantity();
        }
        return (double)Math.round(sum * 100) / 100.0;
    }

    @Override
    public double getRemainingBudget() {
        return (double) Math.round(this.budget * 100) / 100;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }
}
