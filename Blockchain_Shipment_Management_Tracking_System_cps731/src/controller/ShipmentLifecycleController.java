package controller;

import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import gateway.PaymentServiceAdapter;
import model.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsible for shipment lifecycle operations:
 * create, upload docs, update status, query status.
 */
public class ShipmentLifecycleController {

    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorageAdapter storageAdapter;
    private final PaymentServiceAdapter paymentAdapter;
    private final SmartContract smartContract = new SmartContract();

    public ShipmentLifecycleController(BlockchainNetworkGateway blockchainGateway,
            OffChainStorageAdapter storageAdapter,
            PaymentServiceAdapter paymentAdapter) {
        this.blockchainGateway = blockchainGateway;
        this.storageAdapter = storageAdapter;
        this.paymentAdapter = paymentAdapter;
    }

    public String createShipment(User shipper,
            String origin,
            String destination,
            String description) {
        if (!shipper.authorize("SHIPMENT_CREATE")) {
            throw new SecurityException("User not authorized to create shipment");
        }

        String id = UUID.randomUUID().toString();
        Shipment shipment = new Shipment(id, origin, destination, description);
        blockchainGateway.writeShipment(shipment);
        return id;
    }

    public String uploadDocument(User user,
            String shipmentId,
            String name,
            String content) {

        // For now any logged-in user can upload documents
        String docId = UUID.randomUUID().toString();
        Document doc = new Document(docId, shipmentId, name, content);
        storageAdapter.saveDocument(doc);
        return docId;
    }

    public void updateStatus(User actor,
            String shipmentId,
            ShipmentStatus newStatus,
            String description) {

        if (!actor.authorize("SHIPMENT_UPDATE")) {
            throw new SecurityException("User not authorized to update shipment status");
        }

        Shipment shipment = blockchainGateway.getShipment(shipmentId);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found: " + shipmentId);
        }

        if (!smartContract.canUpdateStatus(shipment, newStatus)) {
            throw new IllegalStateException("Smart contract rejected this status transition");
        }

        Event event = new Event(
                UUID.randomUUID().toString(),
                shipmentId,
                newStatus,
                description);
        blockchainGateway.recordEvent(event);

        if (smartContract.canTriggerPayment(shipment)) {
            paymentAdapter.processPayment(shipment);
        }
    }

    public String queryStatus(User user, String shipmentId) {
        if (!user.authorize("SHIPMENT_QUERY")) {
            throw new SecurityException("User not authorized to query shipment");
        }

        Shipment shipment = blockchainGateway.getShipment(shipmentId);
        if (shipment == null) {
            return "Shipment not found.";
        }

        List<Event> events = blockchainGateway.getEvents(shipmentId);

        StringBuilder sb = new StringBuilder();
        sb.append("Shipment ID: ").append(shipment.getShipmentId()).append("\n")
                .append("Origin: ").append(shipment.getOrigin()).append("\n")
                .append("Destination: ").append(shipment.getDestination()).append("\n")
                .append("Description: ").append(shipment.getDescription()).append("\n")
                .append("Current Status: ").append(shipment.getStatus()).append("\n\n")
                .append("Events:\n");

        for (Event e : events) {
            sb.append("- ").append(e.getTimestamp())
                    .append(" | ").append(e.getStatus())
                    .append(" | ").append(e.getDescription())
                    .append("\n");
        }
        return sb.toString();
    }
}
