import java.util.*;

public class Market {
    private Map<String, Stock> stocks;
    private Random random;

    public Market() {
        this.stocks = new HashMap<>();
        this.random = new Random();
        initializeMarket();
    }

    private void initializeMarket() {
        // Initialize with popular stocks
        stocks.put("AAPL", new Stock("AAPL", 175.20));
        stocks.put("GOOG", new Stock("GOOG", 2450.00));
        stocks.put("TSLA", new Stock("TSLA", 720.55));
        stocks.put("MSFT", new Stock("MSFT", 380.75));
        stocks.put("AMZN", new Stock("AMZN", 145.80));
        stocks.put("META", new Stock("META", 320.45));
        stocks.put("NVDA", new Stock("NVDA", 485.90));
        stocks.put("NFLX", new Stock("NFLX", 450.25));
        stocks.put("JPM", new Stock("JPM", 165.30));
        stocks.put("JNJ", new Stock("JNJ", 155.60));
    }

    // Get all stocks
    public Map<String, Stock> getAllStocks() {
        return new HashMap<>(stocks);
    }

    // Get a specific stock
    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    // Check if stock exists
    public boolean stockExists(String symbol) {
        return stocks.containsKey(symbol.toUpperCase());
    }

    // Update all stock prices (simulate market movement)
    public void updateMarket() {
        for (Stock stock : stocks.values()) {
            stock.updatePrice();
        }
    }

    // Get market data as formatted string
    public String getMarketDataString() {
        StringBuilder marketData = new StringBuilder();
        marketData.append("--- Market Data ---\n");
        
        for (Stock stock : stocks.values()) {
            marketData.append(stock.toString()).append("\n");
        }
        
        return marketData.toString();
    }

    // Get available stock symbols
    public List<String> getAvailableSymbols() {
        return new ArrayList<>(stocks.keySet());
    }

    // Get market summary
    public String getMarketSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Market Summary:\n");
        summary.append("Total Stocks: ").append(stocks.size()).append("\n");
        summary.append("Available Symbols: ").append(String.join(", ", getAvailableSymbols())).append("\n");
        return summary.toString();
    }

    // Simulate market hours (optional enhancement)
    public void simulateMarketHours() {
        System.out.println("Market is now open! Prices are updating...");
        updateMarket();
        System.out.println("Market data updated!");
    }
} 
