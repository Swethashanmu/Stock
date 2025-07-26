public class User {
    private String username;
    private String password;
    private Portfolio portfolio;

    public User(String username, String password, double initialCash) {
        this.username = username;
        this.password = password;
        this.portfolio = new Portfolio(initialCash);
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    // Authentication
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    // Change password
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // Get user summary
    public String getUserSummary() {
        return String.format("User: %s\nCash Balance: %s", 
            username, portfolio.getFormattedCashBalance());
    }

    @Override
    public String toString() {
        return String.format("User: %s", username);
    }
} 
