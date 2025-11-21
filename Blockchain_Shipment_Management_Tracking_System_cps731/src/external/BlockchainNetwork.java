package external;

import model.Event;
import model.Shipment;
import model.ShipmentStatus;

import java.util.*;

/**
 * Simulates the underlying blockchain network.
 * In a real system this would talk to actual nodes.
 */
public class BlockchainNetwork {

    private final Map<String, Shipment> shipments = new HashMap<>();
    private final Map<String, List<Event>> eventsByShipment = new HashMap<>();

    public void storeShipment(Shipment shipment) {
        shipments.put(shipment.getShipmentId(), shipment);
        eventsByShipment.put(shipment.getShipmentId(), new ArrayList<>());
        System.out.println("[BlockchainNetwork] Stored shipment " + shipment.getShipmentId());
    }

    public void storeEvent(Event event) {
        List<Event> list = eventsByShipment
                .computeIfAbsent(event.getShipmentId(), k -> new ArrayList<>());
        list.add(event);

        Shipment shipment = shipments.get(event.getShipmentId());
        if (shipment != null) {
            shipment.setStatus(event.getStatus());
            shipment.addEvent(event);
        }

        System.out.println("[BlockchainNetwork] Stored event " + event.getEventId()
                + " for shipment " + event.getShipmentId());
    }

    public Shipment findShipmentById(String shipmentId) {
        return shipments.get(shipmentId);
    }

    public List<Event> findEventsByShipmentId(String shipmentId) {
        return eventsByShipment.getOrDefault(shipmentId, Collections.emptyList());
    }

    public Map<String, Shipment> getAllShipments() {
        return Collections.unmodifiableMap(shipments);
    }
}
