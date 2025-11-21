package gateway;

import external.PaymentService;
import model.Shipment;

/**
 * Adapter around the external PaymentService.
 */
public class PaymentServiceAdapter {

    private final PaymentService paymentService;

    public PaymentServiceAdapter(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void processPayment(Shipment shipment) {
        paymentService.processPayment(shipment);
    }
}
