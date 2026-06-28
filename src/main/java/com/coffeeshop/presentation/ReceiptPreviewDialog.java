package com.coffeeshop.presentation;

import com.coffeeshop.service.ReceiptImageService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReceiptPreviewDialog extends JDialog {
    public ReceiptPreviewDialog(JFrame owner, String receiptText, ReceiptImageService receiptImageService) {
        super(owner, "Receipt Preview", true);
        setSize(700, 620);
        setLocationRelativeTo(owner);

        JTextArea receiptArea = new JTextArea(receiptText);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        receiptArea.setMargin(new Insets(12, 12, 12, 12));
        receiptArea.setBackground(AppTheme.SURFACE);

        JButton close = AppTheme.ghostButton("Close");
        close.addActionListener(e -> dispose());
        JButton print = AppTheme.button("Print simulation", AppTheme.PRIMARY);
        print.addActionListener(e -> JOptionPane.showMessageDialog(this, "Receipt sent to virtual printer."));
        JButton savePng = AppTheme.ghostButton("Save PNG");
        savePng.addActionListener(e -> saveReceiptImage(receiptText, receiptImageService));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(AppTheme.PANEL);
        actions.add(savePng);
        actions.add(print);
        actions.add(close);

        setLayout(new BorderLayout(8, 8));
        add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        add(new QrCodePanel(receiptText), BorderLayout.EAST);
        add(actions, BorderLayout.SOUTH);
    }

    private void saveReceiptImage(String receiptText, ReceiptImageService receiptImageService) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("receipt.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }
        receiptImageService.saveReceiptPng(receiptText, file);
        JOptionPane.showMessageDialog(this, "Saved receipt image:\n" + file.getAbsolutePath());
    }
}
