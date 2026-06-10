package com.coffeeshop;

import com.coffeeshop.presentation.LoginView;
import com.coffeeshop.presentation.AppTheme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTheme.installLookAndFeel();
            new LoginView(new AppContext()).setVisible(true);
        });
    }
}
