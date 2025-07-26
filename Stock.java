import java.util.Random;

public class Stock {
    private String symbol;
    private double price;
    private double previousPrice;
    private double changePercent;
    private Random random;

    public Stock(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.price = initialPrice;
        this.previousPrice = initialPrice;
        this.changePercent = 0.0;
        this.random = new Random();
    }

    // Getters
    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public double getChangePercent() {
        return changePercent;
    }

    // Update stock price with random fluctuation
    public void updatePrice() {
        previousPrice = price;
        // Simulate price change between -5% and +5%
        double changePercent = (random.nextDouble() - 0.5) * 0.1;
        price = price * (1 + changePercent);
        this.changePercent = ((price - previousPrice) / previousPrice) * 100;
        
        // Ensure price doesn't go below $1
        if (price < 1.0) {
            price = 1.0;
        }
    }

    // Format price for display
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getFormattedChange() {
        String sign = changePercent >= 0 ? "+" : "";
        return String.format("%s%.2f%%", sign, changePercent);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", symbol, getFormattedPrice(), getFormattedChange());
    }
} 
