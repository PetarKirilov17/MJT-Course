import bg.sofia.uni.fmi.mjt.trading.Constants;
import bg.sofia.uni.fmi.mjt.trading.Portfolio;
import bg.sofia.uni.fmi.mjt.trading.PortfolioAPI;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChart;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;

public class Main {
    public static void main(String[] args) {
        PriceChartAPI priceChartAPI = new PriceChart(14.56, 15.67, 25.65);

        PortfolioAPI pf1 = new Portfolio("Petar", priceChartAPI, 150, 10);

        pf1.buyStock(Constants.MICROSOFT_TICKER, 3);
        pf1.buyStock(Constants.AMAZON_TICKER, 2);
        pf1.buyStock(Constants.GOOGLE_TICKER, 1);
        System.out.println(pf1.getOwner() +  " has " + pf1.getNetWorth() + " net worth .\n Left budget :" + pf1.getRemainingBudget());
        for (var p:pf1.getAllPurchases()){
            System.out.println(p.getStockTicker() + ":" + p.getTotalPurchasePrice());
        }
    }
}
