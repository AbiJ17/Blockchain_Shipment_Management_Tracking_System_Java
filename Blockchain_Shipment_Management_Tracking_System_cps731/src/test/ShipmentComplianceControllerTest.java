package test;


import org.junit.jupiter.api.*;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import model.Report;
import model.Shipment;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentComplianceControllerTest {

    private ShipmentComplianceController controller;
    private ShipmentLifecycleController lifecycle;

    @BeforeEach
    void setup() {
        controller = new ShipmentComplianceController();
        lifecycle = new ShipmentLifecycleController();
    }

    @Test
    void testQueryShipmentStatus() {
        Shipment s = lifecycle.createShipment(
                new Shipper("alice", "a@x.com"),
                "S100", "X", "Y", "Test");

        s.setStatus("IN_TRANSIT");

        String result = controller.queryShipmentStatus(s);

        assertTrue(result.contains("IN_TRANSIT"));
    }

    @Test
    void testGenerateAuditTrail() {
        Shipment s = lifecycle.createShipment(
                new Shipper("alice", "a@x.com"),
                "S111", "Toronto", "NYC", "Goods");

        lifecycle.updateShipmentStatus(s, "IN_TRANSIT");

        Report r = controller.generateAuditTrail(s);

        assertNotNull(r);
        assertTrue(r.toString().contains("IN_TRANSIT"));
    }

    @Test
    void testLogDispute() {
        Shipment s = lifecycle.createShipment(
                new Shipper("alice", "a@x.com"),
                "S222", "A", "B", "Test");

        String result = controller.logDispute(s, "Damaged item");

        assertTrue(result.contains("Dispute logged"));
    }
}

