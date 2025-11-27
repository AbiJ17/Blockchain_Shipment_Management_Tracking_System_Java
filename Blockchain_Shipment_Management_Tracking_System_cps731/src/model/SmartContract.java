package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmartContract {

    private int contractID;
    private String contractType;
    private List<String> rules = new ArrayList<>();

    // ----- Constructors -----
    public SmartContract() {
    }

    public SmartContract(int contractID, String contractType, List<String> rules) {
        this.contractID = contractID;
        this.contractType = contractType;
        if (rules != null) {
            this.rules = rules;
        }
    }

    /**
     * Business rule for status updates.
     * Now uses String for status (since ShipmentStatus enum was removed).
     *
     * Rule:
     * - If shipment is already DELIVERED, it cannot move back to any other status.
     */
    public boolean canUpdateStatus(Shipment shipment, String newStatus) {
        if (shipment == null || newStatus == null) {
            return false;
        }

        String current = shipment.getStatus();
        if ("DELIVERED".equalsIgnoreCase(current)
                && !"DELIVERED".equalsIgnoreCase(newStatus)) {
            return false;
        }
        return true;
    }

    /** Rule: payment only allowed when status is DELIVERED. */
    public boolean canTriggerPayment(Shipment shipment) {
        if (shipment == null)
            return false;
        return "DELIVERED".equalsIgnoreCase(shipment.getStatus());
    }

    /**
     * Very simple ledger-integrity check used by ShipmentComplianceController:
     * - Events must have non-null timestamps
     * - Timestamps must be strictly increasing (no going backwards).
     */
    public boolean verifyLedgerIntegrity(Shipment shipment) {
        if (shipment == null) {
            return false;
        }

        List<Event> history = shipment.getHistory();
        if (history == null || history.isEmpty()) {
            return true; // nothing to check
        }

        Date prev = null;
        for (Event e : history) {
            Date ts = e.getTimestamp();
            if (ts == null) {
                return false;
            }
            if (prev != null && !ts.after(prev)) {
                return false; // out of order or duplicate timestamp
            }
            prev = ts;
        }
        return true;
    }

    /** Example rule: raise dispute if status is not DELIVERED. */
    public boolean canRaiseDispute(Shipment shipment) {
        return !shipment.getStatus().equals("DELIVERED");
    }

    /**
     * Customs clearance validation rule.
     *
     * Rules:
     * - Decision must be APPROVE or REJECT.
     * - Cannot approve clearance after shipment is DELIVERED.
     * - Approval only allowed when shipment is in a customs-relevant status.
     * - Rejection is always allowed unless shipment is already DELIVERED.
     */
    public boolean validateCustomsClearance(Shipment shipment, String decision) {

        if (shipment == null || decision == null) {
            return false;
        }

        String status = shipment.getStatus().toUpperCase();
        decision = decision.toUpperCase();

        // Must be APPROVE or REJECT
        if (!decision.equals("APPROVE") && !decision.equals("REJECT")) {
            return false;
        }

        // Cannot perform clearance on delivered shipments
        if (status.equals("DELIVERED")) {
            return false;
        }

        // Reject is always allowed prior to delivery
        if (decision.equals("REJECT")) {
            return true;
        }

        // Approval must follow logical customs workflow
        boolean allowedForApproval =
                status.equals("CREATED") ||
                status.equals("IN_TRANSIT") ||
                status.equals("AT_BORDER") ||
                status.equals("AT_WAREHOUSE");

        return allowedForApproval;
    }

    /**
     * Automatic Insurance Claim Rule:
     * A claim is triggered automatically when:
     * - Shipment is DAMAGED, or
     * - Delivery is delayed (delivery timestamp > expected), or
     * - Shipment is NOT DELIVERED by a deadline.
     */
    public boolean triggerInsuranceClaim(Shipment shipment) {

        if (shipment == null)
            return false;

        String status = shipment.getStatus().toUpperCase();

        // Condition 1: Damage detected
        if (status.equals("DAMAGED")) {
            return true;
        }

        // Condition 2: Delayed delivery
        else if (shipment.getExpectedDeliveryDate() != null &&
            shipment.getActualDeliveryDate() != null &&
            shipment.getActualDeliveryDate().after(shipment.getExpectedDeliveryDate())) {
            return true;
        }

        // Condition 3: Non-delivery by deadline
        else if (shipment.getExpectedDeliveryDate() != null &&
            shipment.getActualDeliveryDate() == null &&
            new Date().after(shipment.getExpectedDeliveryDate())) {
            return true;
        }

        return false;
    }

    // ----- Getters/Setters -----
    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

}
