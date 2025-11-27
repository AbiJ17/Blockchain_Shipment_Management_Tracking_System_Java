package test;


import org.junit.jupiter.api.*;

import model.Shipment;
import model.SmartContract;

import static org.junit.jupiter.api.Assertions.*;

public class SmartContractTest {

    private SmartContract contract;
    private Shipment s;

    @BeforeEach
    void setup() {
        contract = new SmartContract();
        s = new Shipment("S900", "A", "B", "Test");
    }

    @Test
    void testValidateStatusUpdate() {
        boolean ok = contract.validateStatusUpdate(s, "IN_TRANSIT");
        assertTrue(ok);
    }

    @Test
    void testLedgerIntegrityPasses() {
        boolean ok = contract.verifyLedgerIntegrity(s);
        assertTrue(ok);
    }

    @Test
    void testImmutabilityPreventsChange() {
        s.addEvent("DELIVERED");
        boolean allowed = contract.enforceImmutability(s, "REOPENED");

        assertFalse(allowed);
    }
}

