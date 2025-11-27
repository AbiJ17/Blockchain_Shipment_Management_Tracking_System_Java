package controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.Document;
import model.Shipment;
import model.Shipper;
import model.SmartContract;

public class ShipmentLifecycleController {

    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;

    // In-memory store of shipments keyed by ID
    private final Map<String, Shipment> shipments = new HashMap<>();

    public ShipmentLifecycleController(BlockchainNetworkGateway blockchainGateway,
            OffChainStorageAdapter offChainAdapter,
            SmartContract smartContract) {
        this.blockchainGateway = blockchainGateway;
        this.offChainAdapter = offChainAdapter;
        this.smartContract = smartContract;
    }

    /**
     * Create and register a new shipment. Called from the Create Shipment screen.
     */
    public Shipment createShipment(Shipper shipper,
            String shipmentID,
            String origin,
            String destination,
            String description) {

        Shipment shipment = new Shipment(shipmentID, origin, destination, description);
        shipment.setStatus("CREATED");
        shipment.addHistoryEvent("Shipment created by shipper " +
                (shipper != null ? shipper.getUsername() : "system"));

        shipments.put(shipmentID, shipment);

        // Simulate writing a transaction to the blockchain
        blockchainGateway.connect();
        blockchainGateway.sendTransaction("CREATE#" + shipmentID);

        return shipment;
    }

    /** Used by MainUI: look up a shipment in memory by ID. */
    public Shipment findShipmentById(String shipmentId) {
        if (shipmentId == null)
            return null;
        return shipments.get(shipmentId);
    }

    /** Used by MainUI: update the shipment status via smart contract rules. */
    public String updateShipmentStatus(Shipment shipment, String newStatus) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        if (newStatus == null || newStatus.isBlank()) {
            return "New status cannot be empty.";
        }

        // Smart-contract rule check
        if (!smartContract.canUpdateStatus(shipment, newStatus)) {
            return "Smart contract rejected status change for shipment " +
                    shipment.getShipmentID();
        }

        shipment.setStatus(newStatus);
        shipment.addHistoryEvent("Status updated to: " + newStatus);

        // Simulate blockchain event
        blockchainGateway.connect();
        blockchainGateway.sendTransaction("STATUS#" + shipment.getShipmentID() +
                "#" + newStatus);

        return "Shipment " + shipment.getShipmentID() +
                " status updated to " + newStatus;
    }

    /** Used by MainUI: create & upload a document for a shipment. */
    public Document uploadDocument(Shipment shipment,
            String documentName,
            String content) {
        if (shipment == null) {
            return null;
        }

        Document doc = new Document();
        doc.setName(documentName);
        doc.setContent(content);
        doc.generateHash();
        doc.setTimestamp(new java.util.Date());

        offChainAdapter.connect();
        offChainAdapter.uploadFile(doc);

        shipment.addDocument(doc);
        shipment.addHistoryEvent("Document uploaded: " + documentName);

        return doc;
    }

    /** Optionally used elsewhere (e.g., admin screens). */
    public Map<String, Shipment> getAllShipments() {
        return Collections.unmodifiableMap(shipments);
    }

    /** Buyer confirms that the shipment has been delivered. */
    public String confirmDelivery(Shipment shipment) {
        if (shipment == null) {
            return "Shipment not found.";
        }

        // Smart Contract validation (optional rule)
        if (!smartContract.canUpdateStatus(shipment, "DELIVERED")) {
            return "Smart contract rejected delivery confirmation.";
        }

        shipment.setStatus("DELIVERED");
        shipment.addHistoryEvent("Delivery confirmed by buyer.");

        // Emit blockchain event
        blockchainGateway.connect();
        blockchainGateway.sendTransaction("DELIVERED#" + shipment.getShipmentID());

        return "Shipment " + shipment.getShipmentID() + " marked as DELIVERED.";
    }

}
