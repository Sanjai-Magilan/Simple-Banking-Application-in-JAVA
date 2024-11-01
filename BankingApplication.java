//package magilan.bankingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

// Custom Exception for invalid transactions
class InvalidTransactionException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidTransactionException(String message) {
        super(message);
    }
}

// Abstract Class for BankAccount
abstract class AbstractBankAccount {
    protected String accountNumber;
    protected double balance;
    protected ArrayList<String> transactionHistory;

    public AbstractBankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with balance: 0");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public abstract void deposit(double amount) throws InvalidTransactionException;

    public abstract void withdraw(double amount) throws InvalidTransactionException;

    protected void addTransaction(String detail) {
        transactionHistory.add(detail);
    }
}

// Concrete implementation of BankAccount
class BankAccount extends AbstractBankAccount {

    public BankAccount(String accountNumber) {
        super(accountNumber);
    }

    @Override
    public void deposit(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be positive.");
        }
        balance += amount;
        addTransaction("Deposited ₹: " + amount);
    }

    @Override
    public void withdraw(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new InvalidTransactionException("Insufficient funds for this withdrawal.");
        }
        balance -= amount;
        addTransaction("Withdrew ₹: " + amount);
    }
}

// GUI class for the Banking Application
@SuppressWarnings("serial")
public class BankingApplication extends JFrame {
    private HashMap<String, BankAccount> accounts;
    private JTextField loginAccountNumberField;
    private JPasswordField passwordField;
    private JPanel mainPanel;
    private BankAccount currentAccount; // Track the current account for the session
    private JDialog accountDialog; // Dialog reference for account info

    public BankingApplication() {
        accounts = new HashMap<>();
        initLoginUI();
    }

    // Initialize the Login UI
    private void initLoginUI() {
        setTitle("GTT Bank - Login");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel(new CardLayout());

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel accountNumberLabel = new JLabel("ACCOUNT NUMBER");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(accountNumberLabel, gbc);

        loginAccountNumberField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(loginAccountNumberField, gbc);

        JLabel passwordLabel = new JLabel("PASSWORD");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("LOGIN");
        loginButton.addActionListener(new LoginListener());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        mainPanel.add(loginPanel, "loginPanel");
        add(mainPanel);
    }

    // Initialize the Home UI after login
    private void initHomeUI() {
        JPanel homePanel = new JPanel(new BorderLayout());

        JLabel bankLabel = new JLabel("THE GTT BANK", SwingConstants.CENTER);
        bankLabel.setFont(new Font("Serif", Font.BOLD, 36));
        homePanel.add(bankLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.BLUE);

        // Adding buttons to the home screen
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> performDeposit());
        buttonPanel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> performWithdraw());
        buttonPanel.add(withdrawButton);

        JButton balanceButton = new JButton("Check Balance");
        balanceButton.addActionListener(e -> viewBalance());
        buttonPanel.add(balanceButton);

        JButton historyButton = new JButton("Transaction History");
        historyButton.addActionListener(e -> viewTransactionHistory());
        buttonPanel.add(historyButton);

        JButton accountButton = new JButton("Account");
        accountButton.addActionListener(e -> viewAccount());
        buttonPanel.add(accountButton);

        homePanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(homePanel, "homePanel");
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "homePanel");
    }

    private void viewAccount() {
        String accountInfo = "Account Number (last 4 digits): " + currentAccount.getAccountNumber().substring(currentAccount.getAccountNumber().length() - 4);
        
        // Create a JPanel to display account information and logout button
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        accountPanel.add(new JLabel(accountInfo));
        
        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> logOut());
        accountPanel.add(logoutButton);
        
        // Show account information in a dialog 
        accountDialog = new JDialog(this, "Account Information", true);
        accountDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        accountDialog.getContentPane().add(accountPanel);
        accountDialog.pack();
        accountDialog.setLocationRelativeTo(this); // Center the dialog
        accountDialog.setVisible(true); // Show the dialog
    }

    private void logOut() {
        accountDialog.dispose(); // Close the account dialog
        currentAccount = null; // Clear current account
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "loginPanel"); // Show login panel
    }

    private void performDeposit() {
        String amountString = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (amountString != null) {
            try {
                double amount = Double.parseDouble(amountString);
                currentAccount.deposit(amount);
                JOptionPane.showMessageDialog(this, "Deposited ₹: " + amount);
            } catch (InvalidTransactionException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void performWithdraw() {
        String amountString = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        if (amountString != null) {
            try {
                double amount = Double.parseDouble(amountString);
                currentAccount.withdraw(amount);
                JOptionPane.showMessageDialog(this, "Withdrew ₹: " + amount);
            } catch (InvalidTransactionException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void viewBalance() {
        JOptionPane.showMessageDialog(this, "Current Balance: ₹" + currentAccount.getBalance());
    }

    private void viewTransactionHistory() {
        StringBuilder history = new StringBuilder("Transaction History:\n");
        for (String transaction : currentAccount.getTransactionHistory()) {
            history.append(transaction).append("\n");
        }
        JOptionPane.showMessageDialog(this, history.toString());
    }

    // Login Action Listener
    private class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String accountNumber = loginAccountNumberField.getText();
            String password = new String(passwordField.getPassword());

            if (accounts.containsKey(accountNumber) && password.equals("password")) { // Hardcoded for simplicity
                currentAccount = accounts.get(accountNumber); // Set the current account
                initHomeUI();
            } else {
                JOptionPane.showMessageDialog(BankingApplication.this, "Invalid Account Number or Password");
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingApplication app = new BankingApplication();
            
            // Add a sample account for testing
            app.accounts.put("12345678", new BankAccount("12345678")); // Using "12345678" as a test account number
            
            app.setVisible(true);
        });
    }
}
