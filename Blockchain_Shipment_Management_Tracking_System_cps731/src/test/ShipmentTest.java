package test;


import org.junit.jupiter.api.*;

import model.Document;
import model.Shipment;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentTest {

    @Test
    void testAddDocument() {
        Shipment s = new Shipment("S1", "A", "B", "Test");

        Document d = new Document("invoice", "hash123");
        s.addDocument(d);

        assertEquals(1, s.getDocuments().size());
    }

    @Test
    void testAddEvent() {
        Shipment s = new Shipment("S1", "A", "B", "Test");
        s.addEvent("CREATED");

        assertEquals(1, s.getEventHistory().size());
    }

    @Test
    void testStatusUpdates() {
        Shipment s = new Shipment("S1", "A", "B", "Test");
        s.setStatus("IN_TRANSIT");

        assertEquals("IN_TRANSIT", s.getStatus());
    }
}

