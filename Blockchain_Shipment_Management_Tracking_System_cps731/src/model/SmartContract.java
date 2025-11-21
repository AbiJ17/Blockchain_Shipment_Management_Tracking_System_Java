package model;

public class SmartContract {

    // Simple rules to show "contract" behaviour

    // Example rule: once DELIVERED, cannot move back to another state
    public boolean canUpdateStatus(Shipment shipment, ShipmentStatus newStatus) {
        if (shipment.getStatus() == ShipmentStatus.DELIVERED &&
                newStatus != ShipmentStatus.DELIVERED) {
            return false;
        }
        return true;
    }

    // Example rule: payment is allowed only when shipment is delivered
    public boolean canTriggerPayment(Shipment shipment) {
        return shipment.getStatus() == ShipmentStatus.DELIVERED;
    }
}
