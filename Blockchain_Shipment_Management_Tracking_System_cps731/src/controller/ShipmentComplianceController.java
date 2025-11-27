package controller;

import java.util.List;

import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.Document;
import model.Report;
import model.Shipment;
import model.SmartContract;

public class ShipmentComplianceController {

    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;

    public ShipmentComplianceController(BlockchainNetworkGateway blockchainGateway, 
            OffChainStorageAdapter offChainAdapter,
            SmartContract smartContract) {

        this.blockchainGateway = blockchainGateway;
        this.offChainAdapter = offChainAdapter; 
        this.smartContract = smartContract;
    }

    // In this simplified version we let the caller pass the Shipment
    // (e.g., retrieved from ShipmentLifecycleController).

    public String queryShipmentStatus(Shipment shipment) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        return "Shipment " + shipment.getShipmentID() +
                " status: " + shipment.getStatus();
    }

    public Report generateAuditTrail(Shipment shipment) {
        if (shipment == null) {
            return new Report("Audit Trail", "No shipment found.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Audit trail for shipment ").append(shipment.getShipmentID()).append("\n\n");

        List<model.Event> history = shipment.getHistory();
        for (model.Event e : history) {
            sb.append(e.getTimestamp())
                    .append("  -  ")
                    .append(e.getMessage())
                    .append("\n");
        }

        // Simulate querying blockchain
        blockchainGateway.queryLedger("AuditTrail#" + shipment.getShipmentID());

        return new Report("Audit Trail - Shipment " + shipment.getShipmentID(), sb.toString());
    }

    public String logDispute(Shipment shipment, String description) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        if (description == null || description.isBlank()) {
            return "Dispute description cannot be empty.";
        }

        // Smart contract validation
        if (!smartContract.canRaiseDispute(shipment)) {
            return "Smart contract rejected dispute for shipment " + shipment.getShipmentID();
        }

        shipment.addHistoryEvent("Dispute raised: " + description);

        // Blockchain log
        blockchainGateway.connect();
        blockchainGateway.sendTransaction("DISPUTE#" + shipment.getShipmentID());

        return "Dispute filed for shipment " + shipment.getShipmentID();
    }

    public String verifyDocument(Shipment shipment, String documentName) {
        if (shipment == null) {
            return "Shipment not found.";
        }
        if (documentName == null || documentName.isBlank()) {
            return "Document name is required.";
        }

        for (Document doc : shipment.getDocuments()) {
            if (doc.getName().equalsIgnoreCase(documentName)) {

                boolean ok = offChainAdapter.verifyIntegrity(doc);

                return ok
                    ? "Document '" + documentName + "' is VALID (hash verified)."
                    : "Document '" + documentName + "' FAILED verification (hash mismatch).";
            }
        }

        return "Document not found: " + documentName;
    }

    public String approveClearance(Shipment shipment, String decision) {
        if (!decision.equals("APPROVE") && !decision.equals("REJECT")) {
            return "Invalid decision. Must be APPROVE or REJECT.";
        }

        boolean allowed = smartContract.validateCustomsClearance(shipment, decision);
        if (!allowed) {
            return "Smart contract rejected clearance.";
        }

        shipment.addHistoryEvent("Customs clearance: " + decision);

        blockchainGateway.sendTransaction("CLEARANCE#" + shipment.getShipmentID() + "#" + decision);

        return "Clearance " + decision + " recorded for shipment " + shipment.getShipmentID();
    }

    public Report generateComplianceSummary(String filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Compliance Report\n\n");

        sb.append("Filter applied: ").append(filter.isEmpty() ? "None" : filter).append("\n\n");
        sb.append("Note: This is a simplified summary. Add more metrics as needed.\n");

        return new Report("Compliance Report", sb.toString());
    }

}
