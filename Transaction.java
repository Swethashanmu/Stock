import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private String stockSymbol;
    private int quantity;
    private double price;
    private TransactionType type;
    private LocalDateTime timestamp;
    private double totalValue;

    public enum TransactionType {
        BUY, SELL
    }

    public Transaction(String stockSymbol, int quantity, double price, TransactionType type) {
        this.transactionId = generateTransactionId();
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.totalValue = quantity * price;
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getFormattedTotalValue() {
        return String.format("$%.2f", totalValue);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %d shares of %s at %s (Total: %s)",
                getFormattedTimestamp(),
                type.toString(),
                quantity,
                stockSymbol,
                getFormattedPrice(),
                getFormattedTotalValue());
    }
} 
