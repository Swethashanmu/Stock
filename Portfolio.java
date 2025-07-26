import java.util.*;

public class Portfolio {
    private Map<String, Integer> holdings; // stock symbol -> quantity
    private double cashBalance;
    private List<Transaction> transactionHistory;
    private double initialCash;

    public Portfolio(double initialCash) {
        this.holdings = new HashMap<>();
        this.cashBalance = initialCash;
        this.initialCash = initialCash;
        this.transactionHistory = new ArrayList<>();
    }

    // Buy stock
    public boolean buyStock(String symbol, int quantity, double price) {
        double totalCost = quantity * price;
        
        if (totalCost > cashBalance) {
            return false; // Insufficient funds
        }

        // Update holdings
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + quantity);
        
        // Update cash balance
        cashBalance -= totalCost;
        
        // Record transaction
        Transaction transaction = new Transaction(symbol, quantity, price, Transaction.TransactionType.BUY);
        transactionHistory.add(transaction);
        
        return true;
    }

    // Sell stock
    public boolean sellStock(String symbol, int quantity, double price) {
        int currentQuantity = holdings.getOrDefault(symbol, 0);
        
        if (quantity > currentQuantity) {
            return false; // Insufficient shares
        }

        // Update holdings
        if (currentQuantity == quantity) {
            holdings.remove(symbol);
        } else {
            holdings.put(symbol, currentQuantity - quantity);
        }
        
        // Update cash balance
        double totalValue = quantity * price;
        cashBalance += totalValue;
        
        // Record transaction
        Transaction transaction = new Transaction(symbol, quantity, price, Transaction.TransactionType.SELL);
        transactionHistory.add(transaction);
        
        return true;
    }

    // Get current holdings
    public Map<String, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }

    // Get cash balance
    public double getCashBalance() {
        return cashBalance;
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    // Calculate total portfolio value
    public double getTotalPortfolioValue(Map<String, Stock> marketData) {
        double totalValue = cashBalance;
        
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            
            Stock stock = marketData.get(symbol);
            if (stock != null) {
                totalValue += quantity * stock.getPrice();
            }
        }
        
        return totalValue;
    }

    // Calculate profit/loss
    public double getProfitLoss(Map<String, Stock> marketData) {
        double currentValue = getTotalPortfolioValue(marketData);
        return currentValue - initialCash;
    }

    // Get profit/loss percentage
    public double getProfitLossPercentage(Map<String, Stock> marketData) {
        if (initialCash == 0) return 0;
        return (getProfitLoss(marketData) / initialCash) * 100;
    }

    // Get formatted cash balance
    public String getFormattedCashBalance() {
        return String.format("$%.2f", cashBalance);
    }

    // Get formatted total portfolio value
    public String getFormattedTotalValue(Map<String, Stock> marketData) {
        return String.format("$%.2f", getTotalPortfolioValue(marketData));
    }

    // Get formatted profit/loss
    public String getFormattedProfitLoss(Map<String, Stock> marketData) {
        double profitLoss = getProfitLoss(marketData);
        String sign = profitLoss >= 0 ? "+" : "";
        return String.format("%s$%.2f", sign, profitLoss);
    }

    // Get formatted profit/loss percentage
    public String getFormattedProfitLossPercentage(Map<String, Stock> marketData) {
        double percentage = getProfitLossPercentage(marketData);
        String sign = percentage >= 0 ? "+" : "";
        return String.format("%s%.2f%%", sign, percentage);
    }

    // Check if user has shares of a stock
    public boolean hasShares(String symbol) {
        return holdings.containsKey(symbol) && holdings.get(symbol) > 0;
    }

    // Get quantity of shares for a stock
    public int getShareQuantity(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }

    // Get portfolio summary as string
    public String getPortfolioSummary(Map<String, Stock> marketData) {
        StringBuilder summary = new StringBuilder();
        summary.append("--- Portfolio Summary ---\n");
        summary.append("Cash: ").append(getFormattedCashBalance()).append("\n");
        summary.append("Total Portfolio Value: ").append(getFormattedTotalValue(marketData)).append("\n");
        summary.append("Profit/Loss: ").append(getFormattedProfitLoss(marketData))
               .append(" (").append(getFormattedProfitLossPercentage(marketData)).append(")\n\n");
        
        if (!holdings.isEmpty()) {
            summary.append("Holdings:\n");
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                String symbol = entry.getKey();
                int quantity = entry.getValue();
                Stock stock = marketData.get(symbol);
                if (stock != null) {
                    summary.append(String.format("  %s: %d shares @ %s\n", 
                        symbol, quantity, stock.getFormattedPrice()));
                }
            }
        } else {
            summary.append("No stock holdings.\n");
        }
        
        return summary.toString();
    }
} 
