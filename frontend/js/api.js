/**
 * API Service connecting to Coffee Shop POS Java Backend
 * If backend is offline, gracefully uses local storage / in-memory store
 */
const API = {
  baseUrl: 'http://localhost:8088/api',
  isOnline: false,

  async checkHealth() {
    try {
      const res = await fetch(`${this.baseUrl}/health`, { signal: AbortSignal.timeout(1200) });
      if (res.ok) {
        this.isOnline = true;
        return true;
      }
    } catch (e) {
      this.isOnline = false;
    }
    return false;
  },

  async getMenu() {
    if (this.isOnline) {
      try {
        const res = await fetch(`${this.baseUrl}/menu`);
        if (res.ok) return await res.json();
      } catch (e) {
        console.warn('API getMenu failed, falling back to local', e);
      }
    }
    return null; // Signals app.js to use default menu
  },

  async getOrders() {
    if (this.isOnline) {
      try {
        const res = await fetch(`${this.baseUrl}/orders`);
        if (res.ok) return await res.json();
      } catch (e) {
        console.warn('API getOrders failed, using local', e);
      }
    }
    return null;
  },

  async submitOrder(orderData) {
    if (this.isOnline) {
      try {
        const res = await fetch(`${this.baseUrl}/orders`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(orderData)
        });
        if (res.ok) return await res.json();
      } catch (e) {
        console.warn('API submitOrder failed, falling back to local', e);
      }
    }
    return null;
  },

  async updateOrderStatus(orderId, nextState) {
    if (this.isOnline) {
      try {
        const res = await fetch(`${this.baseUrl}/orders/${orderId}/state`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ state: nextState })
        });
        if (res.ok) return await res.json();
      } catch (e) {
        console.warn('API updateOrderStatus failed', e);
      }
    }
    return null;
  },

  async processPayment(orderId, gateway, amount) {
    if (this.isOnline) {
      try {
        const res = await fetch(`${this.baseUrl}/orders/${orderId}/pay`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ gateway, amount })
        });
        if (res.ok) return await res.json();
      } catch (e) {
        console.warn('API processPayment failed', e);
      }
    }
    return null;
  }
};
