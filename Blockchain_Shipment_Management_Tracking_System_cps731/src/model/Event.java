package model;

import java.time.LocalDateTime;

public class Event {

    private final String eventId;
    private final String shipmentId;
    private final ShipmentStatus status;
    private final String description;
    private final LocalDateTime timestamp;

    public Event(String eventId, String shipmentId, ShipmentStatus status, String description) {
        this.eventId = eventId;
        this.shipmentId = shipmentId;
        this.status = status;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventId() {
        return eventId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
