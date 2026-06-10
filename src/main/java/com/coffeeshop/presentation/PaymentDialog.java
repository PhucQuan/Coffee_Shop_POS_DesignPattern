package com.coffeeshop.presentation;

import com.coffeeshop.domain.model.Order;
import com.coffeeshop.domain.patterns.adapter.PaymentGateway;
import com.coffeeshop.domain.patterns.adapter.PaymentResult;
import com.coffeeshop.service.PaymentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PaymentDialog extends JDialog {
    private final JLabel statusLabel = new JLabel("Waiting for gateway response...");
    private final JLabel transactionLabel = new JLabel("Transaction: pending");
    private PaymentResult result;

    public PaymentDialog(JFrame owner, Order order, PaymentGateway gateway, PaymentService paymentService) {
        super(owner, gateway.getGatewayName() + " Payment", true);
        setSize(520, 360);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setContentPane(content(order, gateway));
        startPayment(order, gateway, paymentService);
    }

    public PaymentResult getResult() {
        return result;
    }

    private JPanel content(Order order, PaymentGateway gateway) {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(AppTheme.BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel left = AppTheme.card(new BorderLayout(8, 8));
        left.add(new QrCodePanel(gateway.getGatewayName() + ":" + order.getId() + ":" + order.getTotalAmount()), BorderLayout.CENTER);

        JPanel right = AppTheme.card(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel title = new JLabel(gateway.getGatewayName());
        title.setForeground(AppTheme.TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        gbc.gridy = 0;
        right.add(title, gbc);

        JLabel amount = new JLabel(AppTheme.money(order.getTotalAmount()));
        amount.setForeground(AppTheme.ACCENT);
        amount.setFont(amount.getFont().deriveFont(Font.BOLD, 28f));
        gbc.gridy = 1;
        right.add(amount, gbc);

        JLabel helper = AppTheme.muted("Scan the demo QR and wait for confirmation.");
        gbc.gridy = 2;
        right.add(helper, gbc);

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        gbc.gridy = 3;
        right.add(progress, gbc);

        statusLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 4;
        right.add(statusLabel, gbc);

        transactionLabel.setForeground(AppTheme.TEXT);
        transactionLabel.setFont(transactionLabel.getFont().deriveFont(Font.BOLD, 12f));
        gbc.gridy = 5;
        right.add(transactionLabel, gbc);

        JButton close = AppTheme.ghostButton("Close");
        close.setEnabled(false);
        close.addActionListener(e -> dispose());
        gbc.gridy = 6;
        gbc.insets = new Insets(14, 0, 0, 0);
        right.add(close, gbc);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);

        root.putClientProperty("closeButton", close);
        root.putClientProperty("progress", progress);
        return root;
    }

    private void startPayment(Order order, PaymentGateway gateway, PaymentService paymentService) {
        SwingWorker<PaymentResult, Void> worker = new SwingWorker<>() {
            protected PaymentResult doInBackground() throws Exception {
                Thread.sleep(900);
                return paymentService.pay(order, gateway);
            }

            protected void done() {
                try {
                    result = get();
                    statusLabel.setText(result.isSuccess() ? result.getMessage() : "Payment failed: " + result.getMessage());
                    statusLabel.setForeground(result.isSuccess() ? AppTheme.SUCCESS : AppTheme.DANGER);
                    transactionLabel.setText("Transaction: " + result.getTransactionCode());
                } catch (Exception ex) {
                    result = new PaymentResult(false, "N/A", ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage());
                    statusLabel.setText("Payment error: " + result.getMessage());
                    statusLabel.setForeground(AppTheme.DANGER);
                    transactionLabel.setText("Transaction: N/A");
                }
                JComponent content = (JComponent) getContentPane();
                JProgressBar progress = (JProgressBar) content.getClientProperty("progress");
                JButton close = (JButton) content.getClientProperty("closeButton");
                progress.setIndeterminate(false);
                progress.setValue(100);
                close.setEnabled(true);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        };
        worker.execute();
    }
}
