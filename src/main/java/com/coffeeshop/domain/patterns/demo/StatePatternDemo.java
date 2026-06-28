package com.coffeeshop.domain.patterns.demo;

/**
 * Small presentation-friendly example for the State pattern.
 *
 * The real POS code already has the full implementation in
 * com.coffeeshop.domain.patterns.state. This class keeps the idea short:
 * each order status owns the actions that are valid for that status.
 */
public final class StatePatternDemo {
    private StatePatternDemo() {
    }

    public static void main(String[] args) {
        System.out.println(runDemo());
    }

    public static String runDemo() {
        DemoOrder order = new DemoOrder();
        StringBuilder log = new StringBuilder();

        log.append("Initial: ").append(order.getStatus()).append(System.lineSeparator());
        order.prepare();
        log.append("After kitchen starts: ").append(order.getStatus()).append(System.lineSeparator());

        try {
            order.cancel();
        } catch (IllegalStateException ex) {
            log.append("Cancel blocked: ").append(ex.getMessage()).append(System.lineSeparator());
        }

        order.markReady();
        order.pay();
        log.append("Final: ").append(order.getStatus());
        return log.toString();
    }

    private interface DemoOrderState {
        String getName();

        default void prepare(DemoOrder order) {
            throw invalid("prepare");
        }

        default void markReady(DemoOrder order) {
            throw invalid("mark ready");
        }

        default void pay(DemoOrder order) {
            throw invalid("pay");
        }

        default void cancel(DemoOrder order) {
            throw invalid("cancel");
        }

        private IllegalStateException invalid(String action) {
            return new IllegalStateException("Cannot " + action + " when order is " + getName());
        }
    }

    private static final class DemoOrder {
        private DemoOrderState state = new PendingState();

        String getStatus() {
            return state.getName();
        }

        void setState(DemoOrderState state) {
            this.state = state;
        }

        void prepare() {
            state.prepare(this);
        }

        void markReady() {
            state.markReady(this);
        }

        void pay() {
            state.pay(this);
        }

        void cancel() {
            state.cancel(this);
        }
    }

    private static final class PendingState implements DemoOrderState {
        public String getName() {
            return "PENDING";
        }

        public void prepare(DemoOrder order) {
            order.setState(new PreparingState());
        }

        public void cancel(DemoOrder order) {
            order.setState(new CancelledState());
        }
    }

    private static final class PreparingState implements DemoOrderState {
        public String getName() {
            return "PREPARING";
        }

        public void markReady(DemoOrder order) {
            order.setState(new ReadyState());
        }
    }

    private static final class ReadyState implements DemoOrderState {
        public String getName() {
            return "READY";
        }

        public void pay(DemoOrder order) {
            order.setState(new PaidState());
        }
    }

    private static final class PaidState implements DemoOrderState {
        public String getName() {
            return "PAID";
        }
    }

    private static final class CancelledState implements DemoOrderState {
        public String getName() {
            return "CANCELLED";
        }
    }
}
