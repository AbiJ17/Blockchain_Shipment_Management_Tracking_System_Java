package test;


import org.junit.jupiter.api.*;

import gateway.BlockchainNetworkGateway;

import static org.junit.jupiter.api.Assertions.*;

public class BlockchainNetworkGatewayTest {

    private BlockchainNetworkGateway gateway;

    @BeforeEach
    void setup() {
        gateway = new BlockchainNetworkGateway();
    }

    @Test
    void testRecordEvent() {
        gateway.recordEvent("S100", "CREATED");
        assertEquals(1, gateway.fetchEventsForShipment("S100").size());
    }

    @Test
    void testFetchEvents() {
        gateway.recordEvent("S200", "A");
        gateway.recordEvent("S200", "B");

        var list = gateway.fetchEventsForShipment("S200");
        assertEquals(2, list.size());
        assertTrue(list.contains("A"));
    }

    @Test
    void testGetLatestEvent() {
        gateway.recordEvent("S300", "FIRST");
        gateway.recordEvent("S300", "SECOND");

        String latest = gateway.getLatestEvent("S300");

        assertEquals("SECOND", latest);
    }
}
