package com.coffeeshop.domain.patterns.singleton;

public class AppConfig {
    private static volatile AppConfig instance;
    private final String appName = "Coffee Shop POS";
    private final String currency = "VND";

    private AppConfig() {}

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public String getAppName() { return appName; }
    public String getCurrency() { return currency; }
}
