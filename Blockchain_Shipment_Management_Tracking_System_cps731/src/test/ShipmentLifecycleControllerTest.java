package test; 

import model.Shipment;
import model.Shipper;
import model.Document;
import external.BlockchainNetwork;
import external.OffChainStorage;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.SmartContract;

import org.junit.jupiter.api.*;

import controller.ShipmentLifecycleController;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShipmentLifecycleController.
 * 
 * These tests verify core shipment workflow operations:
 *  - Creating shipments
 *  - Retrieving shipments by ID
 *  - Updating shipment status
 *  - Uploading documents to a shipment
 */

public class ShipmentLifecycleControllerTest {

    private ShipmentLifecycleController controller;
    private Shipper shipper;

    @BeforeEach
    void setup() {

        // Real backend objects
        BlockchainNetwork network = new BlockchainNetwork();
        BlockchainNetworkGateway gateway = new BlockchainNetworkGateway(network);
        OffChainStorage storage = new OffChainStorage();
        OffChainStorageAdapter adapter = new OffChainStorageAdapter(storage);
        SmartContract contract = new SmartContract();

        // Controller under test
        controller = new ShipmentLifecycleController(gateway, adapter, contract);

        // A sample shipper performing the actions
        shipper = new Shipper(
                1,
                "alice",
                "alice",
                "alice@example.com",
                "Test Company",
                "123 Test Street"
        );
    }

    /**
     * Test: A shipment should be created and returned with correct fields.
     */
    @Test
    void testCreateShipment() {
        Shipment s = controller.createShipment(
                shipper,
                "S123",
                "Toronto",
                "Vancouver",
                "Electronics"
        );

        assertNotNull(s);
        assertEquals("S123", s.getShipmentID());
        assertEquals("Toronto", s.getOrigin());
        assertEquals("Vancouver", s.getDestination());
    }

    /**
     * Test: After creating a shipment, it must be retrievable by ID.
     */
    @Test
    void testFindShipmentById() {
        controller.createShipment(shipper, "S500", "A", "B", "Test Desc");
        Shipment s = controller.findShipmentById("S500");

        assertNotNull(s);
        assertEquals("S500", s.getShipmentID());
    }

    /**
     * Test: Updating shipment status should change the value
     * and return a meaningful message.
     */
    @Test
    void testUpdateShipmentStatus() {
        Shipment s = controller.createShipment(shipper, "S200", "A", "B", "Test");

        String msg = controller.updateShipmentStatus(s, "IN_TRANSIT");

        assertEquals("IN_TRANSIT", s.getStatus());
        assertTrue(msg.contains("updated"));
    }

    /**
     * Test: Uploading a document should attach it to the shipment.
     */
    @Test
    void testUploadDocument() {
        Shipment s = controller.createShipment(shipper, "S300", "A", "B", "Test");

        Document d = controller.uploadDocument(s, "invoice.pdf", "test-data");

        assertNotNull(d);
        assertEquals(1, s.getDocuments().size());
        assertEquals("invoice.pdf", d.getName());
    }
}

