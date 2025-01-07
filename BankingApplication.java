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
    protected String accountHolderName;
    protected double balance;
    protected ArrayList<String> transactionHistory;

    public AbstractBankAccount(String accountNumber, String accountHolderName) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = 0;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with balance: 0");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
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

    public BankAccount(String accountNumber, String accountHolderName) {
        super(accountNumber, accountHolderName);
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
    private BankAccount currentAccount; 
    private JDialog accountDialog; 

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

    private void viewAccount() {
        // Create a JPanel to display account information and logout button
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        
        // Display account holder name
        JLabel nameLabel = new JLabel("Account Holder: " + currentAccount.getAccountHolderName());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        accountPanel.add(nameLabel);
        
        // Display account number in the next row
        JLabel accountNumberLabel = new JLabel("Account Number: ****" + 
                currentAccount.getAccountNumber().substring(currentAccount.getAccountNumber().length() - 4));
        accountNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        accountPanel.add(accountNumberLabel);
        
        
        accountPanel.add(Box.createVerticalStrut(15));
        
       
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
        logoutButton.addActionListener(e -> logOut()); 
        accountPanel.add(logoutButton);
        
        
        accountDialog = new JDialog(this, "Account Information", true);
        accountDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        accountDialog.getContentPane().add(accountPanel);
        
        
        accountDialog.setPreferredSize(new Dimension(300, 150)); 
        accountDialog.pack();
        accountDialog.setLocationRelativeTo(this); 
        accountDialog.setVisible(true); 
    }
    
    
    private void initHomeUI() {
        JPanel homePanel = new JPanel(new BorderLayout());
    
        JLabel bankLabel = new JLabel("THE GTT BANK", SwingConstants.CENTER);
        bankLabel.setFont(new Font("Serif", Font.BOLD, 36));
        homePanel.add(bankLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(173, 216, 230)); 
    
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
    

    private void logOut() {
        accountDialog.dispose();
        currentAccount = null; 
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "loginPanel"); 
    }

    private void performDeposit() {
        String amountString = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (amountString != null) {
            try {
                double amount = Double.parseDouble(amountString);
                currentAccount.deposit(amount);
                JOptionPane.showMessageDialog(this, "Deposited ₹" + amount);
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
                JOptionPane.showMessageDialog(this, "Withdrew: ₹" + amount);
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

            if (accounts.containsKey(accountNumber) && password.equals("password")) { 
                currentAccount = accounts.get(accountNumber); 
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
            app.accounts.put("12345678", new BankAccount("12345678", "M Kumarasamy"));
            
            app.setVisible(true);
        });
    }
}
