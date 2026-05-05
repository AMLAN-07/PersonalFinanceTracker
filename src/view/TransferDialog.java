package view;

import database.AccountDAO;
import database.TransactionDAO;
import model.Account;
import model.Transaction;
import model.User;
import utils.CurrencyFormatter;
import utils.ExchangeRate;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class TransferDialog extends JDialog {

    private User user;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private boolean transferred = false;

    private JComboBox<String> cmbFromAccount, cmbToAccount;
    private List<Account> accountList;
    private JTextField txtAmount;

    public TransferDialog(JFrame parent, User user) {
        super(parent, "Transfer Funds", true);
        this.user = user;
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();

        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel("Transfer Funds");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);

        // Form panel – clean, modern spacing
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // From Account
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        addFormField(form, gbc, "From:");
        cmbFromAccount = createStyledComboBox();
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cmbFromAccount, gbc);

        // To Account
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        addFormField(form, gbc, "To:");
        cmbToAccount = createStyledComboBox();
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cmbToAccount, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        addFormField(form, gbc, "Amount (" + CurrencyFormatter.getCurrencyCode() + "):");
        txtAmount = new JTextField();
        txtAmount.setFont(UIUtils.F_BODY);
        txtAmount.setBorder(createFieldBorder());
        txtAmount.setPreferredSize(new Dimension(0, 36));
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtAmount, gbc);

        contentPane.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIUtils.F_SMALL);
        cancelBtn.setForeground(UIUtils.TEXT_DARK);
        cancelBtn.setBackground(UIUtils.WHITE);
        cancelBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        cancelBtn.addActionListener(e -> dispose());

        JButton transferBtn = UIUtils.accentButton("Transfer", UIUtils.GREEN);
        transferBtn.setPreferredSize(new Dimension(130, 36));
        transferBtn.addActionListener(e -> performTransfer());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(transferBtn);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        loadAccounts();
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(UIUtils.F_BODY);
        combo.setBackground(UIUtils.WHITE);
        combo.setForeground(UIUtils.TEXT_DARK);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        combo.setPreferredSize(new Dimension(0, 36));
        return combo;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.F_BODY);
        label.setForeground(UIUtils.TEXT_DARK);
        panel.add(label, gbc);
    }

    private Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private void loadAccounts() {
        accountList = accountDAO.getAccountsByUser(user.getId());
        cmbFromAccount.removeAllItems();
        cmbToAccount.removeAllItems();
        for (Account a : accountList) {
            String item = a.getAccountName() + "  (" + CurrencyFormatter.format(a.getBalance()) + ")";
            cmbFromAccount.addItem(item);
            cmbToAccount.addItem(item);
        }
    }

    private void performTransfer() {
        int fromIdx = cmbFromAccount.getSelectedIndex();
        int toIdx = cmbToAccount.getSelectedIndex();

        if (fromIdx == -1 || toIdx == -1) {
            JOptionPane.showMessageDialog(this, "Please select both accounts.");
            return;
        }
        if (fromIdx == toIdx) {
            JOptionPane.showMessageDialog(this, "Cannot transfer to the same account.");
            return;
        }

        String amtText = txtAmount.getText().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        String currencyCode = CurrencyFormatter.getCurrencyCode();
        double amountInINR = ExchangeRate.convertToINR(amount, currencyCode);

        Account fromAcc = accountList.get(fromIdx);
        Account toAcc = accountList.get(toIdx);

        if (amountInINR > fromAcc.getBalance()) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient balance in '" + fromAcc.getAccountName() + "'.\n" +
                            "Current balance: " + CurrencyFormatter.format(fromAcc.getBalance()),
                    "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create expense transaction (money leaves 'from' account)
        Transaction expense = new Transaction();
        expense.setUserId(user.getId());
        expense.setAccountId(fromAcc.getId());
        expense.setCategoryId(null);
        expense.setAmount(amountInINR);                       // ✅ store INR
        expense.setType("EXPENSE");
        expense.setDescription("Transfer to " + toAcc.getAccountName());
        expense.setTransactionDate(Date.valueOf(LocalDate.now()));

        // Create income transaction (money enters 'to' account)
        Transaction income = new Transaction();
        income.setUserId(user.getId());
        income.setAccountId(toAcc.getId());
        income.setCategoryId(null);
        income.setAmount(amountInINR);
        income.setType("INCOME");
        income.setDescription("Transfer from " + fromAcc.getAccountName());
        income.setTransactionDate(Date.valueOf(LocalDate.now()));

        boolean expOk = transactionDAO.add(expense);
        boolean incOk = transactionDAO.add(income);

        if (expOk && incOk) {
            transferred = true;
            JOptionPane.showMessageDialog(this, "Transfer successful.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Transfer failed. Please try again.");
        }
    }

    public boolean isTransferred() {
        return transferred;
    }
}