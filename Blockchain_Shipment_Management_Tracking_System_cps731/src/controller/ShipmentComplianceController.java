package controller;

import gateway.BlockchainNetworkGateway;
import model.Event;
import model.Report;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for compliance / audit-related actions.
 */
public class ShipmentComplianceController {

    private final BlockchainNetworkGateway blockchainGateway;

    public ShipmentComplianceController(BlockchainNetworkGateway blockchainGateway) {
        this.blockchainGateway = blockchainGateway;
    }

    public Report generateAuditTrail(String shipmentId) {
        List<Event> events = blockchainGateway.getEvents(shipmentId);

        if (events.isEmpty()) {
            return new Report("Audit Trail for " + shipmentId,
                    "No events found for this shipment.");
        }

        String body = events.stream()
                .map(e -> e.getTimestamp() + " | " + e.getStatus() + " | " + e.getDescription())
                .collect(Collectors.joining("\n"));

        return new Report("Audit Trail for " + shipmentId, body);
    }
}
