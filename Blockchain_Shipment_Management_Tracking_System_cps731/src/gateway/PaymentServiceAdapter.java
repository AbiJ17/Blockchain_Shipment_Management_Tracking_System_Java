package gateway;

import external.PaymentService;
import model.Document;
import model.Shipment;

/**
 * Adapter around the external PaymentService.
 * Simulates connection & payout processing.
 */
public class PaymentServiceAdapter {

    private boolean connected = false;
    private int lastTransactionID = -1;
    private final PaymentService paymentService;

    public PaymentServiceAdapter(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Simulate connecting to the provider */
    public boolean connect() {
        connected = true;
        return true;
    }

    /** Simulate disconnecting */
    public void disconnect() {
        connected = false;
    }

    /** Process payment and track the transaction ID */
    public boolean processPayment(Shipment shipment, float amount) {
        if (!connected) { 
            return false;
        }
        paymentService.setAmount(amount);
        boolean success = paymentService.processPayment(shipment);
        lastTransactionID = paymentService.getTransactionID();
        return success;
    }

    /** Generate a receipt for the last payment */
    public Document generateReceipt() {
        return paymentService.generateReceipt();
    }

    public int getLastTransactionID() {
        return lastTransactionID;
    }

    public boolean isConnected() {
        return connected;
    }
}

