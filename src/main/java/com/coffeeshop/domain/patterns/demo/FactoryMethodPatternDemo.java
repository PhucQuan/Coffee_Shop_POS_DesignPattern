package com.coffeeshop.domain.patterns.demo;

/**
 * Small presentation-friendly example for the Factory Method pattern.
 *
 * The real POS code already has concrete factories in
 * com.coffeeshop.domain.patterns.factory. This class keeps the demo focused:
 * the caller chooses a category, then a factory creates the right beverage.
 */
public final class FactoryMethodPatternDemo {
    private FactoryMethodPatternDemo() {
    }

    public static void main(String[] args) {
        System.out.println(runDemo());
    }

    public static String runDemo() {
        DemoBeverage coffee = createFromDatabaseCategory("COFFEE", "Ca phe sua", 30000);
        DemoBeverage tea = createFromDatabaseCategory("TEA", "Tra dao", 35000);
        DemoBeverage smoothie = createFromDatabaseCategory("SMOOTHIE", "Sinh to xoai", 45000);

        return coffee.describe() + System.lineSeparator()
                + tea.describe() + System.lineSeparator()
                + smoothie.describe();
    }

    private static DemoBeverage createFromDatabaseCategory(String category, String name, double basePrice) {
        DemoBeverageFactory factory = switch (category) {
            case "COFFEE" -> new CoffeeFactory();
            case "TEA" -> new TeaFactory();
            case "SMOOTHIE" -> new SmoothieFactory();
            default -> throw new IllegalArgumentException("Unsupported beverage category: " + category);
        };
        return factory.create(name, basePrice);
    }

    private interface DemoBeverage {
        String describe();
    }

    private abstract static class DemoBeverageFactory {
        abstract DemoBeverage create(String name, double basePrice);
    }

    private static final class CoffeeFactory extends DemoBeverageFactory {
        DemoBeverage create(String name, double basePrice) {
            return new Coffee(name, basePrice);
        }
    }

    private static final class TeaFactory extends DemoBeverageFactory {
        DemoBeverage create(String name, double basePrice) {
            return new Tea(name, basePrice);
        }
    }

    private static final class SmoothieFactory extends DemoBeverageFactory {
        DemoBeverage create(String name, double basePrice) {
            return new Smoothie(name, basePrice);
        }
    }

    private static final class Coffee implements DemoBeverage {
        private final String name;
        private final double basePrice;

        private Coffee(String name, double basePrice) {
            this.name = name;
            this.basePrice = basePrice;
        }

        public String describe() {
            return "Coffee: " + name + " - " + formatPrice(basePrice);
        }
    }

    private static final class Tea implements DemoBeverage {
        private final String name;
        private final double basePrice;

        private Tea(String name, double basePrice) {
            this.name = name;
            this.basePrice = basePrice;
        }

        public String describe() {
            return "Tea: " + name + " - " + formatPrice(basePrice);
        }
    }

    private static final class Smoothie implements DemoBeverage {
        private final String name;
        private final double basePrice;

        private Smoothie(String name, double basePrice) {
            this.name = name;
            this.basePrice = basePrice;
        }

        public String describe() {
            return "Smoothie: " + name + " - " + formatPrice(basePrice);
        }
    }

    private static String formatPrice(double price) {
        return String.format("%,.0f VND", price);
    }
}
