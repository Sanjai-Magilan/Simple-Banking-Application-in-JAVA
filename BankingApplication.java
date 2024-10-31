import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

// Custom Exception for invalid transactions
class InvalidTransactionException extends Exception {
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
        addTransaction("Deposited Rupees : " + amount);
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
        addTransaction("Withdrew Rupees : " + amount);
    }
}

// GUI class for the Banking Application
public class BankingApplication extends JFrame {
    private HashMap<String, BankAccount> accounts;
    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea outputArea;

    public BankingApplication() {
        accounts = new HashMap<>();
        initUI();
    }

    private void initUI() {
        setTitle("Simple Banking Application");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Account Number Panel
        JPanel accountPanel = new JPanel(new FlowLayout());
        accountPanel.add(new JLabel("Account Number:"));
        accountNumberField = new JTextField(10);
        accountPanel.add(accountNumberField);

        // Amount Panel
        JPanel amountPanel = new JPanel(new FlowLayout());
        amountPanel.add(new JLabel("Amount in Rupees:"));
        amountField = new JTextField(10);
        amountPanel.add(amountField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton createButton = new JButton("Create Account");
        createButton.addActionListener(new CreateAccountListener());
        buttonPanel.add(createButton);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new DepositListener());
        buttonPanel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new WithdrawListener());
        buttonPanel.add(withdrawButton);

        JButton balanceButton = new JButton("View Balance");
        balanceButton.addActionListener(new BalanceListener());
        buttonPanel.add(balanceButton);

        JButton historyButton = new JButton("Transaction History");
        historyButton.addActionListener(new TransactionHistoryListener());
        buttonPanel.add(historyButton);

        // Output Area
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Layout
        setLayout(new BorderLayout());
        add(accountPanel, BorderLayout.NORTH);
        add(amountPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.WEST);

        pack();
    }

    // Action Listeners
    private class CreateAccountListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountNumberField.getText();
            if (!accounts.containsKey(accountNumber)) {
                accounts.put(accountNumber, new BankAccount(accountNumber));
                outputArea.append("Account created with account number: " + accountNumber + "\n");
            } else {
                outputArea.append("Account already exists.\n");
            }
        }
    }

    private class DepositListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountNumberField.getText();
            BankAccount account = accounts.get(accountNumber);
            if (account != null) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    account.deposit(amount);
                    outputArea.append("Deposited Rupees " + amount + " to account " + accountNumber + "\n");
                } catch (InvalidTransactionException ex) {
                    outputArea.append(ex.getMessage() + "\n");
                } catch (NumberFormatException ex) {
                    outputArea.append("Please enter a valid amount.\n");
                }
            } else {
                outputArea.append("Account does not exist.\n");
            }
        }
    }

    private class WithdrawListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountNumberField.getText();
            BankAccount account = accounts.get(accountNumber);
            if (account != null) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    account.withdraw(amount);
                    outputArea.append("Withdrew Rupees " + amount + " from account " + accountNumber + "\n");
                } catch (InvalidTransactionException ex) {
                    outputArea.append(ex.getMessage() + "\n");
                } catch (NumberFormatException ex) {
                    outputArea.append("Please enter a valid amount.\n");
                }
            } else {
                outputArea.append("Account does not exist.\n");
            }
        }
    }

    private class BalanceListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountNumberField.getText();
            BankAccount account = accounts.get(accountNumber);
            if (account != null) {
                outputArea.append("Balance for account " + accountNumber + ": $" + account.getBalance() + "\n");
            } else {
                outputArea.append("Account does not exist.\n");
            }
        }
    }

    private class TransactionHistoryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountNumberField.getText();
            BankAccount account = accounts.get(accountNumber);
            if (account != null) {
                outputArea.append("Transaction history for account " + accountNumber + ":\n");
                for (String transaction : account.getTransactionHistory()) {
                    outputArea.append(transaction + "\n");
                }
            } else {
                outputArea.append("Account does not exist.\n");
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingApplication app = new BankingApplication();
            app.setVisible(true);
        });
    }
}
