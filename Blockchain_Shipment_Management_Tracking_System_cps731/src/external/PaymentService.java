package external;

import model.Shipment;

/**
 * Simulated external payment service.
 */
public class PaymentService {

    public void processPayment(Shipment shipment) {
        // In a real system this would call an external API.
        System.out.println("[PaymentService] Processing payment for shipment "
                + shipment.getShipmentId());
    }
}
