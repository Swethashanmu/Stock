import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Market market;
    private static User currentUser;
    private static Scanner scanner;
    private static ScheduledExecutorService marketScheduler;

    public static void main(String[] args) {
        initializeApplication();
        runMainMenu();
        cleanup();
    }

    private static void initializeApplication() {
        market = new Market();
        scanner = new Scanner(System.in);
        
        // Try to load existing user data
        if (FileHandler.portfolioFileExists()) {
            System.out.println("Loading existing portfolio data...");
            currentUser = FileHandler.loadPortfolioData();
            if (currentUser != null) {
                System.out.println("Welcome back, " + currentUser.getUsername() + "!");
            } else {
                createNewUser();
            }
        } else {
            createNewUser();
        }

        // Start market simulation (optional enhancement)
        startMarketSimulation();
    }

    private static void createNewUser() {
        System.out.println("Welcome to Stock Trading Platform!");
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        
        currentUser = new User(username, "password", 10000.0); // Start with $10,000
        System.out.println("Account created! Starting balance: $10,000");
    }

    private static void startMarketSimulation() {
        marketScheduler = Executors.newScheduledThreadPool(1);
        marketScheduler.scheduleAtFixedRate(() -> {
            market.updateMarket();
        }, 30, 30, TimeUnit.SECONDS); // Update market every 30 seconds
    }

    private static void runMainMenu() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = getValidChoice(1, 8);
            
            switch (choice) {
                case 1:
                    viewMarketData();
                    break;
                case 2:
                    buyStock();
                    break;
                case 3:
                    sellStock();
                    break;
                case 4:
                    viewPortfolio();
                    break;
                case 5:
                    viewTransactionHistory();
                    break;
                case 6:
                    saveData();
                    break;
                case 7:
                    resetPortfolio();
                    break;
                case 8:
                    running = false;
                    break;
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           STOCK TRADING PLATFORM");
        System.out.println("=".repeat(50));
        System.out.println("1. View Market Data");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio");
        System.out.println("5. View Transaction History");
        System.out.println("6. Save Data");
        System.out.println("7. Reset Portfolio");
        System.out.println("8. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Enter your choice: ");
    }

    private static void viewMarketData() {
        System.out.println("\n" + market.getMarketDataString());
        System.out.println("Available symbols: " + String.join(", ", market.getAvailableSymbols()));
    }

    private static void buyStock() {
        System.out.println("\n--- Buy Stock ---");
        System.out.println("Available stocks:");
        System.out.println(market.getMarketDataString());
        
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().toUpperCase();
        
        if (!market.stockExists(symbol)) {
            System.out.println("Error: Stock symbol '" + symbol + "' not found!");
            return;
        }
        
        Stock stock = market.getStock(symbol);
        System.out.println("Current price: " + stock.getFormattedPrice());
        
        System.out.print("Enter quantity to buy: ");
        int quantity = getValidInteger();
        
        if (quantity <= 0) {
            System.out.println("Error: Quantity must be positive!");
            return;
        }
        
        double totalCost = quantity * stock.getPrice();
        System.out.println("Total cost: " + String.format("$%.2f", totalCost));
        
        System.out.print("Confirm purchase? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            Portfolio portfolio = currentUser.getPortfolio();
            if (portfolio.buyStock(symbol, quantity, stock.getPrice())) {
                System.out.println("Transaction successful!");
                System.out.println("Bought " + quantity + " shares of " + symbol + " at " + stock.getFormattedPrice());
            } else {
                System.out.println("Transaction failed! Insufficient funds.");
                System.out.println("Available cash: " + portfolio.getFormattedCashBalance());
            }
        } else {
            System.out.println("Purchase cancelled.");
        }
    }

    private static void sellStock() {
        System.out.println("\n--- Sell Stock ---");
        Portfolio portfolio = currentUser.getPortfolio();
        Map<String, Integer> holdings = portfolio.getHoldings();
        
        if (holdings.isEmpty()) {
            System.out.println("You don't own any stocks to sell!");
            return;
        }
        
        System.out.println("Your holdings:");
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = market.getStock(symbol);
            if (stock != null) {
                System.out.println(symbol + ": " + quantity + " shares @ " + stock.getFormattedPrice());
            }
        }
        
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();
        
        if (!portfolio.hasShares(symbol)) {
            System.out.println("Error: You don't own any shares of " + symbol + "!");
            return;
        }
        
        int availableShares = portfolio.getShareQuantity(symbol);
        Stock stock = market.getStock(symbol);
        
        System.out.println("Available shares: " + availableShares);
        System.out.println("Current price: " + stock.getFormattedPrice());
        
        System.out.print("Enter quantity to sell: ");
        int quantity = getValidInteger();
        
        if (quantity <= 0 || quantity > availableShares) {
            System.out.println("Error: Invalid quantity!");
            return;
        }
        
        double totalValue = quantity * stock.getPrice();
        System.out.println("Total value: " + String.format("$%.2f", totalValue));
        
        System.out.print("Confirm sale? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (portfolio.sellStock(symbol, quantity, stock.getPrice())) {
                System.out.println("Transaction successful!");
                System.out.println("Sold " + quantity + " shares of " + symbol + " at " + stock.getFormattedPrice());
            } else {
                System.out.println("Transaction failed!");
            }
        } else {
            System.out.println("Sale cancelled.");
        }
    }

    private static void viewPortfolio() {
        System.out.println("\n" + currentUser.getPortfolio().getPortfolioSummary(market.getAllStocks()));
    }

    private static void viewTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        List<Transaction> transactions = currentUser.getPortfolio().getTransactionHistory();
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (int i = transactions.size() - 1; i >= 0; i--) {
                System.out.println(transactions.get(i));
            }
        }
    }

    private static void saveData() {
        System.out.println("\n--- Save Data ---");
        
        boolean portfolioSaved = FileHandler.savePortfolioData(currentUser);
        boolean transactionsSaved = FileHandler.saveTransactionHistory(currentUser.getPortfolio().getTransactionHistory());
        boolean userSaved = FileHandler.saveUserData(currentUser);
        
        if (portfolioSaved && transactionsSaved && userSaved) {
            System.out.println("All data saved successfully!");
        } else {
            System.out.println("Some data could not be saved.");
        }
        
        System.out.println(FileHandler.getFileStatus());
    }

    private static void resetPortfolio() {
        System.out.println("\n--- Reset Portfolio ---");
        System.out.println("WARNING: This will delete all your portfolio data!");
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            FileHandler.deletePortfolioFile();
            currentUser = new User(currentUser.getUsername(), "password", 10000.0);
            System.out.println("Portfolio reset! Starting balance: $10,000");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private static int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static int getValidInteger() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static void cleanup() {
        System.out.println("\nSaving data before exit...");
        FileHandler.savePortfolioData(currentUser);
        FileHandler.saveTransactionHistory(currentUser.getPortfolio().getTransactionHistory());
        FileHandler.saveUserData(currentUser);
        
        if (marketScheduler != null) {
            marketScheduler.shutdown();
        }
        
        System.out.println("Thank you for using Stock Trading Platform!");
        scanner.close();
    }
} 
