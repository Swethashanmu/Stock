import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileHandler {
    private static final String PORTFOLIO_FILE = "portfolio_data.txt";
    private static final String TRANSACTION_FILE = "transaction_history.txt";
    private static final String USER_FILE = "user_data.txt";

    // Save portfolio data to file
    public static boolean savePortfolioData(User user) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PORTFOLIO_FILE))) {
            Portfolio portfolio = user.getPortfolio();
            
            // Save user info
            writer.println("USER:" + user.getUsername());
            writer.println("CASH:" + portfolio.getCashBalance());
            
            // Save holdings
            Map<String, Integer> holdings = portfolio.getHoldings();
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                writer.println("HOLDING:" + entry.getKey() + ":" + entry.getValue());
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving portfolio data: " + e.getMessage());
            return false;
        }
    }

    // Load portfolio data from file
    public static User loadPortfolioData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PORTFOLIO_FILE))) {
            String line;
            String username = "default";
            double cashBalance = 10000.0; // Default starting cash
            Map<String, Integer> holdings = new HashMap<>();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    switch (parts[0]) {
                        case "USER":
                            username = parts[1];
                            break;
                        case "CASH":
                            cashBalance = Double.parseDouble(parts[1]);
                            break;
                        case "HOLDING":
                            if (parts.length >= 3) {
                                String symbol = parts[1];
                                int quantity = Integer.parseInt(parts[2]);
                                holdings.put(symbol, quantity);
                            }
                            break;
                    }
                }
            }
            
            User user = new User(username, "password", cashBalance);
            Portfolio portfolio = user.getPortfolio();
            
            // Restore holdings
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                // Note: We can't restore the exact purchase prices, so we'll use current market prices
                // In a real implementation, you'd want to store purchase prices too
                portfolio.buyStock(entry.getKey(), entry.getValue(), 0.0); // Price will be updated by market
            }
            
            return user;
        } catch (IOException e) {
            System.err.println("Error loading portfolio data: " + e.getMessage());
            return null;
        }
    }

    // Save transaction history
    public static boolean saveTransactionHistory(List<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTION_FILE))) {
            for (Transaction transaction : transactions) {
                writer.println(String.format("TXN:%s:%s:%d:%.2f:%s:%s",
                    transaction.getTransactionId(),
                    transaction.getStockSymbol(),
                    transaction.getQuantity(),
                    transaction.getPrice(),
                    transaction.getType().toString(),
                    transaction.getFormattedTimestamp()));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving transaction history: " + e.getMessage());
            return false;
        }
    }

    // Load transaction history
    public static List<Transaction> loadTransactionHistory() {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 6) {
                    String symbol = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    Transaction.TransactionType type = Transaction.TransactionType.valueOf(parts[4]);
                    
                    Transaction transaction = new Transaction(symbol, quantity, price, type);
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transaction history: " + e.getMessage());
        }
        
        return transactions;
    }

    // Save user data
    public static boolean saveUserData(User user) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            writer.println("USERNAME:" + user.getUsername());
            // Note: In a real application, you'd want to hash the password
            writer.println("PASSWORD:" + user.getUsername()); // Simplified for demo
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            return false;
        }
    }

    // Check if portfolio file exists
    public static boolean portfolioFileExists() {
        return new File(PORTFOLIO_FILE).exists();
    }

    // Delete portfolio file (for reset functionality)
    public static boolean deletePortfolioFile() {
        File file = new File(PORTFOLIO_FILE);
        return file.delete();
    }

    // Get file status
    public static String getFileStatus() {
        StringBuilder status = new StringBuilder();
        status.append("File Status:\n");
        status.append("Portfolio file exists: ").append(portfolioFileExists()).append("\n");
        status.append("Transaction file exists: ").append(new File(TRANSACTION_FILE).exists()).append("\n");
        status.append("User file exists: ").append(new File(USER_FILE).exists()).append("\n");
        return status.toString();
    }
} 
